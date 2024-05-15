/*
 *   MIT License
 *
 *   Copyright (c) 2024 Darshan Pania
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
*/

package com.darshan.notificity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.room.TypeConverter
import java.nio.ByteBuffer

class Converters {
    @TypeConverter
    fun bitmapToBase64(bitmap: Bitmap): String {
        // create a ByteBuffer and allocate size equal to bytes in   the bitmap
        val byteBuffer = ByteBuffer.allocate(bitmap.height * bitmap.rowBytes)
        // copy all the pixels from bitmap to byteBuffer
        bitmap.copyPixelsToBuffer(byteBuffer)
        // convert byte buffer into byteArray
        val byteArray = byteBuffer.array()
        // convert byteArray to Base64 String with default flags
        return Base64.encodeToString(byteArray, DEFAULT)
    }

    @TypeConverter
    fun base64ToBitmap(base64String: String): Bitmap {
        // convert Base64 String into byteArray
        val byteArray = Base64.decode(base64String, DEFAULT)
        // byteArray to Bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
