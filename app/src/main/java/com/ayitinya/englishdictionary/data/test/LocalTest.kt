package com.ayitinya.englishdictionary.data.test

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.SkipQueryVerification


@Entity(
    tableName = "_Dict_zstd",
    foreignKeys = [ForeignKey(
        entity = ZstdDicts::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("_data_dict"),
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index(name = "_data_dict_idx", value = ["_data_dict"], orders = [Index.Order.ASC])]
)
data class DictionaryZstd(
    @PrimaryKey val id: Int,
    val word: String,
    val data: String,
    @ColumnInfo(name = "_data_dict", defaultValue = "null") val dataDict: Int?
)

@Entity(tableName = "_zstd_dicts")
data class ZstdDicts(
    @PrimaryKey val id: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val dict: ByteArray,
    @ColumnInfo(name = "chooser_key") val chooserKey: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZstdDicts

        if (id != other.id) return false
        if (!dict.contentEquals(other.dict)) return false
        if (chooserKey != other.chooserKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + dict.contentHashCode()
        result = 31 * result + (chooserKey?.hashCode() ?: 0)
        return result
    }
}

/*
* Verification was skipped because the custom sqlite function cannot be validated by room
* https://stackoverflow.com/a/56083245/13605694
* */
@SkipQueryVerification
@DatabaseView(
    "SELECT id, word, zstd_decompress_col(data, 1, _data_dict, true) as data from _Dict_zstd",
)
data class LocalTest(
    val id: Int,
    val word: String,
    val data: String,
)
