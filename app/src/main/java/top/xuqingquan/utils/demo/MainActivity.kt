package top.xuqingquan.utils.demo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import top.xuqingquan.utils.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
//    private val key = "19fe872d1d9da0db".toByteArray()
//    private val iv = "4646a98799a80f54".toByteArray()
//    private val transformation = "AES/CBC/PKCS5Padding"
//    private val o_str =
//        """{"al": 32620, "s": [{"sv": 32612, "n": "快速鉴别指南~哪种蚊子咬了我？？", "u": "BV1gz4y1Q7FF", "t": "科技", "up": "491716321", "un": "猫哭兄弟"}, {"sv": 32611, "n": "【睡前消息144】山东大学减少留学生补贴，为什么还是要被骂？", "u": "BV1uD4y1D7AK", "t": "科技", "up": "54992199", "un": "观视频工作室"}, {"sv": 32610, "n": "一些有趣的心理学小知识，很实用了", "u": "BV15v411q74H", "t": "生活", "up": "412719797", "un": "YouTube精彩视频-"}, {"sv": 32609, "n": "【呜哝x奶果】summertime（夏日限定超甜舞蹈）", "u": "BV1mp4y1i7F4", "t": "舞蹈", "up": "900171", "un": "果哝双子"}, {"sv": 32608, "n": "印度疫情爆发 日本疫情触底反弹 大明白胖亚瑟首次梦幻激情合体", "u": "BV1SV411B7GL", "t": "生活", "up": "254726274", "un": "东京大明白"}, {"sv": 32607, "n": "我们的小老虎有了自己的中国名字！", "u": "BV1V5411e7df", "t": "生活", "up": "590490400", "un": "克里米亚野生动物园"}, {"sv": 32606, "n": "赶紧看，某影视公司内部视频流出", "u": "BV16v411q79N", "t": "生活", "up": "883968", "un": "暴走漫画"}, {"sv": 32605, "n": "人类早期驯服野生熊本熊的珍贵影像", "u": "BV12h411o7Nk", "t": "生活", "up": "474267806", "un": "人类早期驯服"}, {"sv": 32604, "n": "深夜惨遭刷差评，优质的改编究竟扎了谁的心？【天宝伏妖录】", "u": "BV1VD4y1D7vt", "t": "国创", "up": "379963545", "un": "九日耀空"}, {"sv": 32603, "n": "【片片】南极罗生门，狂杀七人全身而退，这才叫完美犯罪！揭秘解", "u": "BV1hK4y1x7K5", "t": "影视", "up": "10119428", "un": "小片片说大片"}, {"sv": 32602, "n": "《X档案：隐形人弹钢琴之谜》——你一定听过的bgm系列", "u": "BV1Sz4y1D7qv", "t": "音乐", "up": "320491072", "un": "绯绯Feifei"}, {"sv": 32601, "n": "斯内普死前 还有一惊人细节，完美解说《哈利波特》，7个月制作", "u": "BV1Wt4y1X7Fw", "t": "影视", "up": "17588331", "un": "青蛙刀圣_1993"}, {"sv": 32600, "n": "【阴阳师】全新SSR 紧那罗：绝美CG结合佛教传说全面详细解", "u": "BV1JD4y1D7F5", "t": "游戏", "up": "50111839", "un": "小琨爱小蛊"}, {"sv": 32599, "n": "“我在红尘之中，等你回来。”【国漫唯美古风】", "u": "BV1RZ4y1u7mD", "t": "国创", "up": "2390163", "un": "蒙娜丽鲨鱼"}, {"sv": 32598, "n": "这是一场心理与生理的极限比拼...", "u": "BV1pv411q7rw", "t": "生活", "up": "10330740", "un": "观察者网"}, {"sv": 32597, "n": "【明日方舟】危机合约利刃行动技能专精推荐，浮士德改拿RPG了", "u": "BV1oD4y1m7Aw", "t": "游戏", "up": "43222001", "un": "卡特亚"}, {"sv": 32596, "n": "我和周淑怡看着你，你尴尬吗？【第四期】", "u": "BV125411h75F", "t": "生活", "up": "365135915", "un": "欣小萌有点污"}, {"sv": 32595, "n": "浙江大学又上热搜？犯了强奸罪，依旧可以顺利答辩、毕业、入职走", "u": "BV1rh411o7yP", "t": "科技", "up": "16451540", "un": "小球盲杨毛毛"}, {"sv": 32594, "n": "天鹅之梦重做完成！新版大招变身白天鹅特效炸裂！回城双鹅共舞！", "u": "BV1Mt4y1X7p2", "t": "游戏", "up": "12799703", "un": "包子入侵SSR"}, {"sv": 32593, "n": "吊车尾参军那点事（十四）", "u": "BV13E411K73N", "t": "生活", "up": "95480382", "un": "左拆家"}]}"""
//    private val d_str="vRMNibDqEKyBN0Mn73KIZz4n/6K5LT1ReCXvYfLf/+lWDHTrvBtDxO50XGEbJZ6og9qXM/I600mRX6TO/lR9JMLn3Cwdkq6tWQHi5t2gQs2uWc/+Ay1Ui3RhXQ2wiVKlIYZSpwNYhz+mSITOl52OgrPyZTzvSHYcQ7I7LcrIv6hIQwA7ZG+VoYG37FsjIMVp0C6rQGnZUTGSWYfn+48O6Y5RoJFGMdt01RLz7D0uHadl5O4WQOQEJ3Jg88Q9PZQWZL4KQhDjoKEUtN5WzXRmaqjWSLvbYLoA5e4/bLwdl0UbnQ+mN4f4sIJdqQiID5fWWj8kJBJjVWnDJS8INoGbcLuMIa2bplSy9gYZ8O+4+hJ1NAzqBq1cS8eLdPUkiPZEjvDfqNj6E9L6ra8tKyGagYph3O98UHZfs/pzFFVBKc4jPV+jgfXFcDEZwn9MWNgUbD+L3kPAMp/gPO5dj8AIcfG355VtZ3nZ/DiDv6hG6UJB1SvuksqK/2K0z8VY3Rg6/JzYjC3c+pTGFRwztUf0rNGew4K4hTeRrGrMb0OmPZNVSYVeK7S9UDa7ckkv9ak6nS0rUOvD3AQ91mzjUXYoxpkaAxcdrc6odVaN36gbFOzZ0D6S1bgKy0CKqf9zymur1P2Cjmg7uql6WrOR/Hp5NmOLE222H/mNBdtmIMxiAr9C4QdOQo5k/pEAJUN2N66bcPOqmyysarkhHP0+Os6L46XyHnrzyDtp1nMKdoZ1KIs9ipjw2gTKn1mkQLcI7TVshZEe2hWDlxRHJEE7cJYsE2fG9Ok9GzvIlRlAdYYW280VW5ECZ4CdYBpuplVI075zIY4gs9OspfGMis1Spzol9lTgKyhZ6WIBi5GQjbAd87th25mtlyrl/zld44F0cTBcvNPsGFhAbVsKm3CScvlX5jiLAA58xvG0dN/MS6E7yTc6mLu142BD/XQThv1y7oqtdeSXldyoDT3xo0+QbNT6PlCGhFPoUFmU3xZOtJlhave86X+X3WVP5svDvuLYoJHn58NE8iRRCysLBOkv5/nfgZ44kRaAWX6eahTNojPikO6pHxXbg2YC4c5EzA3jS0XneMU0TdT91iuCmuOVkmuM913aZPFvrgxW8odb7XLgdZrboqqMRDoyCE0sBjGpDIl14pUiwiiziL/riBgWI8A11sFeJtbscPak3CFXC4PQmTfAN7kkvGYIm0iDFOfTsxuoHj/TuPTEoi+tNuqj/l4iNQWIRh2Zva+inaADJ85XrEl95jzM+EGn1LtB0plQMUQ1WxRZu8tQ4plR6VxRnlL0ByLr0PYJhZaQG9FEehoRpNqIdo4ubOOjtJrICBw4cdGJCRyDz53RElfhILScvo/lOX6vhc9NtKFrOkt9HMQM8cE+MLd6tO9u1IFYKZv5u4x3ySjNretnYb+9+AZHoiBKccAkcANZdu46G+IUh2s0SgfZN4sYy8CIWpUSmwP64UWPpWX7dPZhMwxy+CxxlntS6t3kzy0uaj9OOLcTzc7ZbaR8RU9B+Yf7iMKASAyoikyrnBP973iij2UFlCQOrgHASKdti3AMPxYRSXNFBWOijNlN4I8Fm6RvxVU5m0JZ+15PkpXPpqlOWqRx0TUL0p7OgqGIzOLcqPNGeHU7z+4z7rHIMGq3d7y7ag6TwJn2QSiTc2/XG+Pu1rzl/eraf8rOTlKQ9gA+YmnUqPJLxOd1ma7NNWWaV6lGJm5YctMrrik6H4U/RQqj8vMLF28rvROWXlCWRbqfFXLQNEdrusXe/vYYyRRrcU+4JpwP6Rq5wroyzViyxebPwr4LReQPXI57deaZvH2D94Szaqa4nZXUATeZa3HatdUxfyXBJyNrcTsuqCKBBwu140fAfdwlxcpXxHO8Fzwx1T2A2zQIa7sKQiXZjMhQWHMrPDDyZoE+2xcqOD8LyBYpoIi0GIRwerVjSL4WtqHETX1D1xj+SSgONS4VoGGoMNHu3g1kbukR2wCUiJ4tCzVRUVnsuLChi6G/EFH6CYfa9LAys6Ys7wUGMivRzbWsfsUcThUnv7Hg88n8WiswktZxgTBHSyxPWgnkjWGM+QB57ZKb/Zs/U5hGKImTX18VHU4FoGxq1H1jqdT8CRSdNL35kBmavh6Q6sOk7t1PCBEZFo+If44wL543s5w9DIZXXzZCf0XjVMp6Qfz6vZbRdueNx+3zaRPViWfBmJMjOK2pyywovKfXke/v2dPJDdO6bVzGw63piK97DesFzgufHtvQZs2gO8xi2Dx8xcBTcGDYlMLBJX/9w9peqKEtbNA8a9NhFJo37N0ZUIRflAxY9dS1ulb1j9lMMsnoZsZUoJWs6Jw2fVnrsCM1Z5/6EUdsRNrCjDPHhD4Ma6zwpwfGPHnvmMnEFzdyyMMGFX8g1Y4WoRNN7mkm2NhvFMCap/EuHCYR8raT+nnVyte2B3tXpMvGxwrDRlEUdwBG3Jzq+5DBe2Ljrh685hnhQ4c="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
//        val compressByteArray = ZipHelper.compressForZlib(o_str)
//        val encryptAES2Base64 =
//            EncryptUtils.encryptAES2Base64(compressByteArray, key, transformation, iv)
//        val str=String(encryptAES2Base64)
//        println("base641===>$str")
//        val decryptBase64AES =
//            EncryptUtils.decryptBase64AES(d_str.toByteArray(), key, transformation, iv)
//        val decompressByteArray = ZipHelper.decompressForZlib(decryptBase64AES)
//        val decompressStr=String(decompressByteArray)
//        println("decompressStr===>$decompressStr")
        tv.setOnClickListener {
//            toast("${NotchScreenUtil.checkNotchScreen(this)}")
            if (hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Timber.d("hasPermission")
                choosePhoto()
            } else {
                Timber.d("requestPermissions")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0x1
                )
            }
        }
//        Timber.d("versionCode=" + getVersionCode(this))
//        val stringRes =
//            resources.getIdentifier("app_name", "string", packageName)
//        Timber.d("appName=>${getString(stringRes)}")
//        Timber.d("width=>${getScreenWidth(this)}")
//        Timber.d("height=>${getScreenHeight(this)}")
//        Timber.d("statusBarHeight=>"+StatusBarUtils.getStatusbarHeight(this))
        StatusBarUtils.setNavigationBackgroundColor(this,ContextCompat.getColor(this,R.color.black))
        StatusBarUtils.setNavigationIconDark(this,true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x1) {
            if (hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                choosePhoto()
            }
        } else if (requestCode == 0x2 && data?.data != null) {
            try {
                Timber.d("data===>${data.data!!}")
                val path = getPath(this, data.data!!) ?: ""
                Timber.d("path=>${path}")
                val file = File(path)
                val uri = Uri.fromFile(file)
                Timber.d("uri=>${uri}")
                val path2 = getFileMediaUrl(this, uri)
                Timber.d("path2=>${path2}")
                Glide.with(this).load(path2).into(iv)
//                val cursor=contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),MediaStore.Images.Media.DATA + "=? ",
//                arrayOf(uri.path),null)
//                if (cursor==null){
//                    Timber.d("cursor==null")
//                    return
//                }
//                if (cursor.moveToFirst()){
//                    val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
//                    val baseUri = Uri.parse("content://media/external/images/media")
//                    val uri2=Uri.withAppendedPath(baseUri, "" + id)
//                    Timber.d("uri2=>${uri2}")
//                    val path2 = uriToPath(this, uri)
//                    Timber.d("path2=${path2}")
//                    val file2 = File(path2)
//                    Glide.with(this).load(file2).into(iv)
//                }else{
//                    Timber.d("cursor.moveToFirst()")
//                }
//                cursor.close()
            } catch (e: Throwable) {
            }
        }
    }
    /*
    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

     */

    private fun choosePhoto() {
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intentToPickPic, 0x2)
    }


}