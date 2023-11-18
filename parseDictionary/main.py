import json
import sqlite3
from typing import List, Union, Dict, Literal, TypedDict, NotRequired, TypeVar, Optional
from count_lines import rawgencount

from os import path

T = TypeVar("T")


def not_none(obj: Optional[T]) -> T:
    assert obj is not None
    return obj


class Example(TypedDict):
    text: str
    type: Literal["example", "text", "ref"]


class Sense(TypedDict):
    raw_glosses: Union[List[str], None]
    glosses: List[str]
    examples: List[Dict[Literal["type", "text", "ref"], str]]
    tags: NotRequired[List[str]]


class Data(TypedDict):
    pos: str
    word: str
    sounds: List[Dict[Literal["ipa", "ogg_url"], str]]
    etymology_text: str
    senses: List[Sense]
    source: Union[str, None]


Sound = TypedDict("Sound", {"ipa": str | None, "ogg_url": str | None})
# ParsedSense = List[str]


class ParsedSense(TypedDict):
    raw_glosses: List[str]
    examples: List[str]


class ParsedData(TypedDict):
    etymology: str | None
    sound: str | None
    pos: str
    senses: List[ParsedSense]


def main():
    filename = path.join("data", "kaikki.org-dictionary-English-words.json")
    number_of_lines = rawgencount(filename=filename)
    error_count = 0

    connection = sqlite3.connect("dictionary.sqlite")
    connection.enable_load_extension(True)
    libsqlite_zstd_path = path.join("lib", "libsqlite_zstd.so")
    connection.load_extension(libsqlite_zstd_path)

    cursor = connection.cursor()
    cursor.execute("PRAGMA journal_mode=WAL;")
    cursor.execute("PRAGMA auto_vacuum=full;")

    cursor.execute(
        """
            CREATE TABLE IF NOT EXISTS Dict (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                word TEXT NOT NULL,
                data TEXT NOT NULL
            )
        """
    )

    line_number = 0
    with open(file=filename, encoding="utf-8") as f:
        for line in f:
            line_number += 1

            data: Data = json.loads(line)

            if "source" in data and data["source"] == "thesaurus":
                continue

            if "tags" in data["senses"][0] and "no-gloss" in data["senses"][0]["tags"]:
                continue

            if "word" not in data:
                continue

            parsed_data: ParsedData = {
                "sound": None,
                "etymology": data["etymology_text"]
                if "etymology_text" in data
                else None,
                "pos": data["pos"],
                "senses": [],
            }

            if "sounds" in data:
                for sound in data["sounds"]:
                    if "ipa" in sound:
                        parsed_data["sound"] = sound["ipa"]
                        break

            for sense in data["senses"]:
                parsed_sense: ParsedSense = {
                    "examples": [],
                    "raw_glosses": [],
                }

                if "raw_glosses" in sense:
                    parsed_sense["raw_glosses"] = not_none(sense["raw_glosses"])
                elif "glosses" in sense:
                    parsed_sense["raw_glosses"] = sense["glosses"]

                if "examples" in sense:
                    if (
                        "type" in sense["examples"][0]
                        and sense["examples"][0]["type"] == "example"
                    ):
                        if sense["examples"][0]["type"] == "example":
                            parsed_sense["examples"].append(sense["examples"][0]["text"])
                        elif sense["examples"][0]["type"] == "quotation":
                            quotation = f"{sense['examples'][0]['text']} - {sense['examples'][0]['ref']}"
                            parsed_sense["examples"].append(quotation)
                parsed_data["senses"].append(parsed_sense)

            print(round((line_number / number_of_lines) * 100, 2))

            try:
                cursor.execute(
                    "INSERT INTO Dict (word, data) VALUES (?, ?)",
                    [data["word"], json.dumps(parsed_data)],
                )
            except Exception as e:
                error_count += 1
                if error_count > 5:
                    raise Exception("Too many errors")
                print(f"Exception: {data['word']} - {e}")

    connection.commit()
    cursor.execute("VACUUM")

    cursor.execute(
        f'SELECT zstd_enable_transparent(\'{{"table": "Dict", "column": "data", "compression_level": 19, "dict_chooser": "\'\'a\'\' || (id / 1000000)"}}\');'
    )
    cursor.execute("SELECT zstd_incremental_maintenance(null, 1)")
    cursor.execute("VACUUM")

    connection.close()
    print(error_count)


if __name__ == "__main__":
    main()
