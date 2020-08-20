package com.yuewen.cooperate.pathstat.pagestate

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log

/**
 * @author zhanglulu on 2020/8/19.
 * for 解决跨进程通信
 */
private const val TAG = "PathStatContent"
class PathStateContentProvider: ContentProvider() {

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        return ProcessorPool.getMethod(method)?.process(arg, extras)
    }

    //----------------------------------------------------------------------------------------------
    // 不使用 start
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException()
    }
    // 不使用 end
    //----------------------------------------------------------------------------------------------

}