package aluno.ifsc.app.focos.dengue.resources.utilidades

import aluno.ifsc.app.focos.dengue.BuildConfig
import aluno.ifsc.app.focos.dengue.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.ShareCompat

object ImageUtil {
    fun getResizedBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val scale = Math.min(maxHeight.toFloat() / bitmap.width, maxWidth.toFloat() / bitmap.height)

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun scaleImage(realImage: Bitmap, maxHeight: Float, maxWidth: Float): Bitmap? {
        try {
            var actualHeight = realImage.height.toFloat()
            var actualWidth = realImage.width.toFloat()

            var imgRatio = actualWidth / actualHeight
            val maxRatio = maxWidth / maxHeight

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt().toFloat()
                    actualHeight = maxHeight.toInt().toFloat()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt().toFloat()
                    actualWidth = maxWidth.toInt().toFloat()
                } else {
                    actualHeight = maxHeight.toInt().toFloat()
                    actualWidth = maxWidth.toInt().toFloat()
                }
            } else {
                while (actualHeight < maxHeight && actualWidth < maxWidth) {
                    actualHeight += 1f
                    actualWidth += 1f
                }
            }
            return Bitmap.createScaledBitmap(realImage, actualWidth.toInt(), actualHeight.toInt(), true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun compressImage(filePath: String): String {
        try {
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

            val maxHeight = 816.0f * 1.90f
            val maxWidth = 612.0f * 1.90f

            var imgRatio = (actualWidth / actualHeight).toFloat()
            val maxRatio = maxWidth / maxHeight

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            options.inJustDecodeBounds = false
//            options.inPurgeable = true
//            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)

            try {
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }

            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

            val exif: ExifInterface
            try {
                exif = ExifInterface(filePath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                Log.d("EXIF", "Exif: $orientation")
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }

                scaledBitmap =
                    Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var out: FileOutputStream?

            try {
                out = FileOutputStream(filePath)
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return filePath
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    fun getResizeBitmap(uri: Uri, content: ContentResolver): Bitmap? {
        return resizeImage(uri, content)
    }

    fun getResizeBitmap(path: String, content: ContentResolver): Bitmap? {
        val uri = Uri.fromFile(File(path))
        var bitmap: Bitmap? = null
        bitmap = resizeImage(uri, content)
        return bitmap
    }

    fun getResizeBitmapRotate(aCurrentPhotoPath: String, aContent: ContentResolver): Bitmap? {
        val lBmOptions = BitmapFactory.Options()
        lBmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(aCurrentPhotoPath, lBmOptions)
        var lPhotoW = lBmOptions.outWidth
        var lPhotoH = lBmOptions.outHeight

        val scaleFactor = Math.max(lPhotoW / 800, lPhotoH / 800)

        lBmOptions.inJustDecodeBounds = false
        lBmOptions.inSampleSize = scaleFactor
        lBmOptions.inDensity = 72

        var lPhoto = getResizeBitmap(aCurrentPhotoPath, aContent)

        val orientation = imageOrientationValidator(aCurrentPhotoPath)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                lPhoto = rotateImage(lPhoto, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> lPhoto = rotateImage(lPhoto, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                lPhoto = rotateImage(lPhoto, 270f)
            }
        }
        return lPhoto
    }

    private fun resizeImage(uri: Uri, content: ContentResolver): Bitmap? {
        var `in`: InputStream? = null
        try {
            val IMAGE_MAX_SIZE = 1200000 // 1.2MP
            `in` = content.openInputStream(uri)

            // tamanho da imagem!
            var o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`in`, null, o)
            `in`!!.close()

            var scale = 1
            while (o.outWidth * o.outHeight * (1 / Math.pow(scale.toDouble(), 2.0)) > IMAGE_MAX_SIZE) {
                scale++
            }

            var b: Bitmap? = null
            `in` = content.openInputStream(uri)
            if (scale > 1) {
                scale--
                // escala para máximo inSampleSize possível que ainda produz uma imagem
                // na largura do item
                o = BitmapFactory.Options()
                o.inSampleSize = scale
                b = BitmapFactory.decodeStream(`in`, null, o)

                // redimensionar as dimensões desejadas
                val height = b!!.height
                val width = b.width
                Log.d("TAG", "1th scale operation dimenions - width: $width, height: $height")

                val y = Math.sqrt(IMAGE_MAX_SIZE / (width.toDouble() / height))
                val x = y / height * width

                val scaledBitmap = Bitmap.createScaledBitmap(
                    b, x.toInt(),
                    y.toInt(), true
                )
                b.recycle()
                b = scaledBitmap

                System.gc()
            } else {
                b = BitmapFactory.decodeStream(`in`)
            }
            `in`!!.close()
            return b
        } catch (e: OutOfMemoryError) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return null
    }

    fun compressPhoto(aPhoto64: String): String {
        val lBmOptions = BitmapFactory.Options()
        lBmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(aPhoto64, lBmOptions)
        var lPhotoW = lBmOptions.outWidth
        var lPhotoH = lBmOptions.outHeight

        val lScaleFactor = Math.max(lPhotoW / 800, lPhotoH / 800)

        lBmOptions.inJustDecodeBounds = false
        lBmOptions.inSampleSize = lScaleFactor
        lBmOptions.inDensity = 75

        val lDecodePhoto = Base64.decode(aPhoto64, Base64.DEFAULT)
        var lBitmap: Bitmap? = BitmapFactory.decodeByteArray(lDecodePhoto, 0, lDecodePhoto.size, lBmOptions)

        val lOrientation = imageOrientationValidator(aPhoto64)

        when (lOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                val aux = lPhotoH
                lPhotoH = lPhotoW
                lPhotoW = aux
                lBitmap = rotateImage(lBitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> lBitmap = rotateImage(lBitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                lBitmap = rotateImage(lBitmap, 270f)
            }
        }
        lBitmap = getResizedBitmap(lBitmap!!, 1000, 1000)
        val lByteArrayOutputStream = ByteArrayOutputStream()
        lBitmap.compress(Bitmap.CompressFormat.JPEG, 90, lByteArrayOutputStream)
        val lByteArray = lByteArrayOutputStream.toByteArray()
        return Base64.encodeToString(lByteArray, Base64.DEFAULT)
    }

    fun imageOrientationValidator(path: String): Int {

        val ei: ExifInterface
        try {
            ei = ExifInterface(path)
            return ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return 0
    }

    fun convertBase64ToBitmap(image: String): Bitmap {
        val decodedString = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {

        var bitmap: Bitmap? = null
        val matrix = Matrix()
        matrix.postRotate(angle)
        try {
            bitmap = Bitmap.createBitmap(
                source!!, 0, 0, source.width, source.height,
                matrix, true
            )
        } catch (err: OutOfMemoryError) {
            err.printStackTrace()
        }

        return bitmap
    }

    fun writeFile(input: InputStream, file: File) {
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file)
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var len: Int
            do {
                len = input.read(buffer)
                out.write(buffer, 0, len)
            } while (len > 0)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("Recycle", "InlinedApi")
    fun getOrientation(context: Context, photoUri: Uri): Int {

        val cursor = context.contentResolver.query(
            photoUri,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION), null, null, null
        )

        if (cursor == null ) {
            if(1 !== cursor?.count) {
                return 90  //Assuming it was taken portrait
            }
        }

        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    @Throws(IOException::class)
    fun getCorrectlyOrientedImage(context: Context, photoUri: Uri, maxWidth: Int): Bitmap? {

        var `is` = context.contentResolver.openInputStream(photoUri)
        val dbo = BitmapFactory.Options()
        dbo.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, dbo)
        `is`!!.close()


        val rotatedWidth: Int
        val rotatedHeight: Int
        val orientation = getOrientation(context, photoUri)

        if (orientation == 90 || orientation == 270) {
            Log.d("ImageUtil", "Will be rotated")
            rotatedWidth = dbo.outHeight
            rotatedHeight = dbo.outWidth
        } else {
            rotatedWidth = dbo.outWidth
            rotatedHeight = dbo.outHeight
        }

        var srcBitmap: Bitmap?
        `is` = context.contentResolver.openInputStream(photoUri)
        Log.d(
            "ImageUtil", String.format(
                "rotatedWidth=%s, rotatedHeight=%s, maxWidth=%s",
                rotatedWidth, rotatedHeight, maxWidth
            )
        )
        if (rotatedWidth > maxWidth || rotatedHeight > maxWidth) {
            val widthRatio = rotatedWidth.toFloat() / maxWidth.toFloat()
            val heightRatio = rotatedHeight.toFloat() / maxWidth.toFloat()
            val maxRatio = Math.max(widthRatio, heightRatio)
            Log.d(
                "ImageUtil", String.format(
                    "Shrinking. maxRatio=%s",
                    maxRatio
                )
            )

            // Create the bitmap from file
            val options = BitmapFactory.Options()
            options.inSampleSize = maxRatio.toInt()
            srcBitmap = BitmapFactory.decodeStream(`is`, null, options)
        } else {
            Log.d(
                "ImageUtil", String.format(
                    "No need for Shrinking. maxRatio=%s",
                    1
                )
            )

            srcBitmap = BitmapFactory.decodeStream(`is`)
            Log.d("ImageUtil", String.format("Decoded bitmap successful"))
        }
        `is`!!.close()

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())

            srcBitmap = Bitmap.createBitmap(
                srcBitmap!!, 0, 0, srcBitmap.width,
                srcBitmap.height, matrix, true
            )
        }

        return srcBitmap
    }


    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei = ExifInterface(image_absolute_path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)!!
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)!!
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)!!
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
            else -> bitmap
        }
    }

    private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun gallery(context: Context, data : Uri?) : Bitmap? {
        try {
            val inputStream = context.contentResolver.openInputStream(data!!)
            val file = File(context.externalCacheDir, "tempFile.jpg")
            writeFile(inputStream!!, file)
            val filePath = file.absolutePath

            val lBmOptions = BitmapFactory.Options()

            lBmOptions.inJustDecodeBounds = true

            BitmapFactory.decodeFile(filePath, lBmOptions)

            var lPhotoW = lBmOptions.outWidth
            var lPhotoH = lBmOptions.outHeight

            val scaleFactor = max(lPhotoW / 1000, lPhotoH / 1000)

            lBmOptions.inJustDecodeBounds = false
            lBmOptions.inSampleSize = scaleFactor
            lBmOptions.inDensity = 72

            var bitmap: Bitmap? = BitmapFactory.decodeFile(filePath, lBmOptions) ?: return null

            when (imageOrientationValidator(filePath)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    bitmap = rotateImage(bitmap, 90f)
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    bitmap = rotateImage(bitmap, 180f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    bitmap = rotateImage(bitmap, 270f)
                }
            }
            bitmap = BitmapFactory.decodeFile(filePath)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, ByteArrayOutputStream())
            try {
                bitmap = getResizedBitmap(bitmap!!, 500, 500)
                var bitmapRotate = modifyOrientation(bitmap, filePath)
                return bitmapRotate
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun camera(aFile: File, aCurrentPhotoPath : String) : Bitmap? {
        try {
            val lBmOptions = BitmapFactory.Options()

            lBmOptions.inJustDecodeBounds = true

            BitmapFactory.decodeFile(aFile.absolutePath, lBmOptions)

            var lPhotoW = lBmOptions.outWidth
            var lPhotoH = lBmOptions.outHeight

            val scaleFactor = max(lPhotoW / 1000, lPhotoH / 1000)

            lBmOptions.inJustDecodeBounds = false
            lBmOptions.inSampleSize = scaleFactor
            lBmOptions.inDensity = 72

            var bitmap: Bitmap? = BitmapFactory.decodeFile(aCurrentPhotoPath, lBmOptions) ?: return null
            when (imageOrientationValidator(aCurrentPhotoPath)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    bitmap = rotateImage(bitmap, 90f)
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    bitmap = rotateImage(bitmap, 180f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    bitmap = rotateImage(bitmap, 270f)
                }
            }
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, ByteArrayOutputStream())
            try {
                bitmap = getResizedBitmap(bitmap!!, 500, 500)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        } catch (ex: IOException) {
            println(ex.message)
        }
        return null
    }

    fun sendImage(activity: Activity, path : String) {
        val uri = FileProvider.getUriForFile(
            activity.applicationContext,
            BuildConfig.APPLICATION_ID + ".fileprovider",
            File(path)
        )
        val share = ShareCompat.IntentBuilder.from(activity)
            .setStream(uri) // uri from FileProvider
            .setType("text/html")
            .intent
            .setAction(Intent.ACTION_SEND) //Change if needed
            .setDataAndType(uri, "image/*")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        activity.startActivity(Intent.createChooser(share, activity.getString(R.string.prompt_send_image)))
    }

    fun checkPremission(context : Activity, create : Create) {
        val permissionStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionCamera = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(
                context,
                permissionCamera
            ) == PackageManager.PERMISSION_DENIED
            ||
            ContextCompat.checkSelfPermission(
                context,
                permissionStorage
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(permissionCamera, permissionStorage), 4
            )
        } else {
            //DONE: Solicitar da camera ou galeria
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var photoFile: File? = null
            try {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val storageDir = context.getExternalFilesDir("external_files")
                val image = File.createTempFile(imageFileName, ".jpg", storageDir)
                create.absolutePath(image.absolutePath)
                println(image.absolutePath)
                photoFile = image
            } catch (ex: IOException) {
                println(ex.message)
            }
            if (photoFile != null) {
                val photoURI: Uri? =
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        Uri.fromFile(photoFile)
                    } else {
                        FileProvider.getUriForFile(
                            context,
                            BuildConfig.APPLICATION_ID + ".fileprovider", photoFile
                        )
                    }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                context.startActivityForResult(intent, ActivityRequestCode.CAMERA)
            }
        }
    }

    interface Action {
        fun gallery(aIntent : Uri?)
        fun camera(aFile : File)
    }

    interface Create {
        fun absolutePath(aPath : String?)
    }

}
