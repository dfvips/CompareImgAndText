package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class upload {

	public static JsonObject getToken() {
//		String jsonpath = getWebPath.getWebPaths("token.json");
//		if (jsonpath==null) {
//			jsonpath="/usr/local/tomcat/webapps/Api/WEB-INF/token.json";
//		}
		String json = Jsonfile.json;
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		if (obj.get("data") != null) {
			JsonObject data = obj.get("data").getAsJsonObject();
			Long expire = data.get("expire").getAsLong();
//			String policy = data.get("policy").getAsString();
//			String accessid = data.get("accessid").getAsString();
//			String signature = data.get("signature").getAsString();
			Long currentTime = new Date().getTime() / 1000;
			if (currentTime < expire) {
				return data;
			} else {
				return updateToken(json);
			}
		} else {
			return updateToken(json);
		}
	}

	public static JsonObject updateToken(String json) {
		String api = "https://open-s.1688.com/openservice/ossDataService?appName=pc_tusou&appKey="
				+ encodeBase64("ossDataService;" + new Date().getTime());
		Document doc = null;
		Connection connection = Jsoup.connect(api);
		connection = connection.header("referer", "https://www.1688.com");
		connection = connection.header("Content-Type", "application/json;charset=UTF-8");
		connection = connection.header("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36");
		try {
			doc = connection.timeout(500000000).ignoreContentType(true).get();
			json = doc.text();
			Jsonfile.json = json;
			return getToken();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return getToken();
		}
	}

	public static String getsign(String AccessToken,String Cookie) {
		if(AccessToken==null) {
			AccessToken="TLEXYUELH7EH7IOZDVX5MGFSJZPVRVOIJNUPPVALZB6R2RRIVRRQ11065a0";
		}
		if (Cookie==null) {
			Cookie="api_uid=rBQRm197ZHkyHBt1MsHSAg==";
		}
		String api = "https://api.pinduoduo.com/file/signature?pdduid=5002102670437&xcx=20161201&xcx_version=v6.7.8.2&xcx_hash="
				+ gethash(16);
		//33KPHBZAHI5ROJQIAFKOS62FFREEP23J7R5GF7M27KWWZLHYWZDA 110461f
		//BS6FMEUDDVCTJDV3QZQ5YVSAPQ2FXUEGSXMUFIJEFHYWXHSVCV4Q 112428c
		//NKLCWEGJ7KPZJALUFO7IW37QXSOPVAA3JLBZP5I2G7OACCFKX4YA 1127161
		//NKLCWEGJ7KPZJALUFO7IW37QXSOPVCB3JLBZP5I2G7OACCFKX4FD1127184
		Document doc = null;
		String signature = null;
		Connection connection = Jsoup.connect(api);
		connection = connection.header("Content-Type", "application/json;charset=UTF-8");
		connection = connection.header("Accept", "*/*");
		connection = connection.header("Accept-Language", "en-us");
		connection = connection.header("User-Agent",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.17(0x17001126) NetType/WIFI Language/en");
		connection = connection.header("AccessToken", AccessToken);
		connection = connection.header("Referer", "https://servicewechat.com/wx32540bd863b27570/1271/page-frame.html");
		connection = connection.header("Cache-Control", "no-cache");
		connection = connection.header("Cookie", Cookie);
		connection = connection
				.requestBody("{\"bucket_tag\":\"search-img-extractor-app\",\"xcx_version\":\"v6.7.8.2\"}");
		try {
			doc = connection.timeout(500000000).ignoreContentType(true).post();
			String json = doc.text();
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			signature = obj.get("signature").getAsString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			signature = "71d4c84bbcf5ffbd4825050c738b9a75f87d3907";
		}
		return signature;
	}

	public static String gethash(int length) {
		Random random = new Random();
		String val = "";
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(9) % 3 == 0 ? "num" : "char";
			if ("char".equalsIgnoreCase(charOrNum)) {
				int choice = 97;
				if (random.nextInt(9) % 2 == 0) {
					val += String.valueOf((char) (choice + random.nextInt(26))).toUpperCase();
				} else {
					val += (char) (choice + random.nextInt(26));
				}
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return new Date().getTime() + val;
	}

	public static String upFile(String path) {
		int beginIndex = path.lastIndexOf("/") + 1;
		int endIndex = path.lastIndexOf(".");
		String name = null;
		try {
			name = path.substring(beginIndex, endIndex);
		} catch (Exception e) {
			// TODO: handle exception
			name = "41c92602-800d-4391-b827-3d5e45107a89";
		}
		String binary = readImage(path, null);
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				@SuppressWarnings("unused")
				MediaType mediaType = MediaType.parse("multipart/form-data;boundary=----WebKitFormBoundaryH7KecGC1VU0nc012;charset=UTF-8");
				RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				  .addFormDataPart("imgfile",name,
				    RequestBody.create(MediaType.parse("application/octet-stream"),
				    new File(binary)))
				  .build();
				Request request = new Request.Builder()
				  .url("http://s.taobao.com/image")
				  .method("POST", body)
				  .addHeader("accept", "*/*")
				  .addHeader("accept-language", "zh-CN,zh;q=0.9")
				  .addHeader("content-type", "multipart/form-data;boundary=----WebKitFormBoundaryH7KecGC1VU0nc012:")
				  .addHeader("origin", "https://s.taobao.com")
				  .addHeader("referer", "https://s.taobao.com")
				  .addHeader("cookie", "t=90ad81ccb3b04473bb8b49a800886413; cna=VTctGFRXoBICAd/VeJteq24T; lgc=qq420443292; tracknick=qq420443292; _cc_=Vq8l%2BKCLiw%3D%3D; thw=cn; UM_distinctid=17622d999701b6-0877894e58a98c-c791e37-144000-17622d9997134e; _m_h5_tk=e6757aa5814cc75a6a79164a1a4ff8d0_1607494237327; _m_h5_tk_enc=20681709723e532e2e44d10228b2e249; v=0; _tb_token_=370015b47e6a4; _samesite_flag_=true; cookie2=182d9e327b397c9481ede3a917feba9b; sgcookie=E100tX3GihomZW3tLc5NyhaEo%2BzOeJV16oVR6Zio0Vz8uwA9UpZyiP4SIo%2BQzX8ooPe8AJz36JpcbWZy2Gzco1j9VQ%3D%3D; uc3=lg2=VFC%2FuZ9ayeYq2g%3D%3D&id2=Uoe8iq7AMQYr9w%3D%3D&nk2=EuRW4ijvt3EzFqo%3D&vt3=F8dCuAJ08p2MYJLRkfE%3D; csg=2c08abc9; dnk=qq420443292; skt=3888703b9a0c14df; existShop=MTYwNzQ4NTEyMA%3D%3D; uc4=nk4=0%40EJv1iv2d1lVgnhBc5HAxgwMCzlEzqg%3D%3D&id4=0%40UO%2B6bNeDfyBRdijL1kTqkLOXjxpQ; _uab_collina=160748512183443764092433; enc=l5S9FIr2I0t6Y1U3pprFpGgERPaTEj3bUUkmWWpCnC%2F6dO9BBYW9BzAGsSYx12cKHl67O%2BX4%2FzntAR1r5VNfSg%3D%3D; JSESSIONID=AC572ADE7A86630977138A69799B9F22; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; hng=CN%7Czh-CN%7CCNY%7C156; mt=ci=-1_0; uc1=cookie16=UIHiLt3xCS3yM2h4eKHS9lpEOw%3D%3D&cookie21=Vq8l%2BKCLivbdjeuVIQ2NTQ%3D%3D&cookie14=Uoe0al0hvQWnzg%3D%3D&existShop=false&pas=0; xlly_s=1; tfstk=c8v5BF_NPz4WNLGUU3iVYBYGkRBCZB3fmb_JVQDyCnrqHTt5ilwN5XXptr2A6i1..; l=eBTbvCLHO6BXQOgbBO5Cnurza77teIRbzoVzaNbMiInca6BO9FNZgNQ2hWheWdtjgt5x_etrJmGnARn2-xz38xi27_QHRs5mpd96Re1..; isg=BM3NGXuIrn1mDQomAR56Xwih3OlHqgF8ysOJWg9Ss2TZBu-41_tMTW_UcJpgwhk0; x5sec=7b227061696c6974616f3b32223a2262663133393132616537303238656535303361366664336461376535353435334350363678763446454a5863782f2b776c4e58454c786f4d4d5459344d4455304f5445794e6a7330227d")
				  .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)")
				  .build();
		Response response = null;
		String json = "";
		try {
			response = client.newCall(request).execute();
			json = response.body().string();
			System.out.println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(binary);
		file.delete();
//		System.out.println(json);
		// https://s.taobao.com/search?q=&imgfile=&commend=all&ssid=s5-e&search_type=item&tfsid=O1CN01wccWiY2HHl7mx2hiw_!!1680549126.jpg&app=imgsearch
		try {
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			if (obj.get("name") != null) {
				String na = obj.get("name").getAsString();
				return "https://s.taobao.com/search?imgfile=&js=1&style=grid&stats_click=search_radio_all%3A1&initiative_id=staobaoz_20161215&ie=utf8&app=imgsearch&tfsid="+na;
				
			} else {
				String url = obj.get("url").getAsString();
				if (url.contains("_____tmd_____") || url.contains("x5")) {
					url = "https://s.taobao.com" + url;
				}
				return url;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return json;
//			e.printStackTrace();
//			return "https://s.taobao.com/search?q=&imgfile=&commend=all&ssid=s5-e&search_type=item&tfsid=500&app=imgsearch";
		}
	}

	public static String upFile(String path, JsonObject data) {
		if (data.get("policy") != null) {
			String policy = data.get("policy").getAsString();
			String accessid = data.get("accessid").getAsString();
			String signature = data.get("signature").getAsString();
			FileInputStream binary = upload.readImage(path);
			Connection.Response response = null;
			String name = path.substring(path.lastIndexOf("/"), path.length())
					.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5£¬,¡££¿¡°¡±]+", "");
			if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
				name = path.substring(path.lastIndexOf("/") + 1, path.length());
			} else {
				name = path.substring(path.lastIndexOf("/") + 1, path.length()) + ".jpeg";
			}
			String key = getKey();
			try {
				response = Jsoup.connect("https://cbusearch.oss-cn-shanghai.aliyuncs.com")
						.header("Content-Type", "multipart/form-data;")
						.header("Referer", "https://s.1688.com/youyuan/index.htm")
						.header("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36")
						.timeout(10 * 1000).method(Connection.Method.POST).data("key", key).data("name", name)
						.data("success_action_status", "200").data("callback", "").data("OSSAccessKeyId", accessid)
						.data("policy", policy).data("signature", signature).data("file", "file", binary).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response.statusCode() == 200) {
				System.out.println(response.body());
				return "https://s.1688.com/youyuan/index.htm?tab=imageSearch&imageType=oss&imageAddress=" + key;
			} else {
				return "https://s.1688.com/youyuan/index.htm";
			}
		} else {
			return "https://s.1688.com/youyuan/index.htm";
		}
	}

	public static String upFile(String path, String sign,String cookie) {
		String api = "https://file.pinduoduo.com/general_file?sign=" + sign;
		if(cookie==null) {
			cookie="api_uid=rBQRm197ZHkyHBt1MsHSAg==";
		}
		FileInputStream binary = upload.readImage(path);
		try {
			Connection.Response response = Jsoup.connect(api).header("Content-Type", "multipart/form-data;").header(
					"User-Agent",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.17(0x17001126) NetType/WIFI Language/en")
					.header("Referer", "https://servicewechat.com/wx32540bd863b27570/1271/page-frame.html")
					.header("Accept-Language", "en-us").header("Cookie", cookie)
					.timeout(10 * 1000).method(Connection.Method.POST).data("file", "a.jpg", binary).ignoreContentType(true).execute();
			String json = response.parse().text();
//			System.out.println(json);
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			String url = obj.get("url").getAsString();
			return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "500";
		}
	}

	public static String getKey() {
		String s = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
		char[] arr = s.toCharArray();
		String res = "cbuimgsearch/";
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			char cha = arr[r.nextInt(s.length())];
			res += cha;
		}
		res += String.valueOf(new Date().getTime()).substring(0, 7) + "000000";
		return res;
	}
	
	public static String getAccessToken() {
		String arr[] = new String[4];
		arr[0]="5YDAF2SOVUXXN3EF7QWGAHFJFH7JQUGG3GOXRFTOVN53CHP2U2BQ1127a57";
		arr[1]="33KPHBZAHI5ROJQIAFKOS62FFREEP23J7R5GF7M27KWWZLHYWZDA110461f";
		arr[2]="BS6FMEUDDVCTJDV3QZQ5YVSAPQ2FXUEGSXMUFIJEFHYWXHSVCV4Q112428c";
		arr[3]="NKLCWEGJ7KPZJALUFO7IW37QXSOPVAA3JLBZP5I2G7OACCFKX4YA1127161";
		Random r = new Random();
		return arr[r.nextInt(4)];
	}

	public static FileInputStream readImage(String path) {
		File file = null;
		String dir = null;
		FileInputStream fis = null;
		try {
			URL url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(1000 * 5);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.connect();
			URLConnection con = url.openConnection();
			con.connect();
			BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
			String name = path.substring(path.lastIndexOf("/"), path.length())
					.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5£¬,¡££¿¡°¡±]+", "");
			if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
				name = path.substring(path.lastIndexOf("/") + 1, path.length());
			} else {
				name = path.substring(path.lastIndexOf("/") + 1, path.length()) + ".jpg";
			}
			dir = getWebPath.getWebPaths(null) + name;
			file = new File(dir);
			OutputStream out = new FileOutputStream(file);
			int size = 0;
			byte[] buf = new byte[2048];
			while ((size = bin.read(buf)) != -1) {
				out.write(buf, 0, size);
			}
			bin.close();
			out.close();
			fis = getImageBinary(file);
			file.delete();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("download error");
			return null;
		}
		return fis;
	}

	public static String readFile(String path) {
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String line = bufferedReader.readLine();
			fileInputStream.close();
			return line;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static boolean writeFile(String path, String content) {
		try {
			File file = new File(path);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes());
			fileOutputStream.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public static FileInputStream getImageBinary(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			return fis;
		} catch (IOException e) {
//			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String encodeBase64(String s) {
		byte[] b = s.getBytes();
		new Base64();
		return Base64.encode(b);
	}

	public static String readImage(String path, String type) {
		File file = null;
		String dir = null;
		try {
			URL url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(1000 * 5);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.connect();
			URLConnection con = url.openConnection();
			con.connect();
			BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
			String name = path.substring(path.lastIndexOf("/"), path.length())
					.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5£¬,¡££¿¡°¡±]+", "");
			if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
				name = path.substring(path.lastIndexOf("/") + 1, path.length());
			} else {
				name = path.substring(path.lastIndexOf("/") + 1, path.length()) + ".jpg";
			}
			dir = getWebPath.getWebPaths(null) + name;
			file = new File(dir);
			OutputStream out = new FileOutputStream(file);
			int size = 0;
			byte[] buf = new byte[2048];
			while ((size = bin.read(buf)) != -1) {
				out.write(buf, 0, size);
			}
			bin.close();
			out.close();
			return dir;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// System.out.println("download error");
			return null;
		}
	}

	public static void dologin() {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("text/plain");
		RequestBody body = RequestBody.create(mediaType, "");
		Request request = new Request.Builder().url(
				"http://login.taobao.com//member/loginByIm.do?_input_charset=utf-8&uid=cntaobaoqq420443292&webpas=a1ac483da64db9407d4d1711b06cf2b6173229208&token=da8552b72e7486693252fc9a2f33be8e&time=1602559063169&url=https://healthcenter.taobao.com//home/widget_flex.htm?from=assistant&act=SignIn&asker=TaobaoAssistant&asker_version=5.0&errurl=http://www.taobao.com")
				.method("POST", body)
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("User-Agent",
						"Mozilla/5.0(WindowsNT6.3;WOW64)AppleWebKit/537.36(KHTML,likeGecko)Chrome/44.0.2403.155Aef/3.25tbaAppSafari/537.36")
				.addHeader("Referer",
						"http://assist5item.taobao.com/jtbas/myAssistant.htm?sessionId=168054912605597&pageType=aef")
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6")
				.addHeader("Cookie",
						"cna=mWIIGLFSfTcCATo+ITEwzb6H;uc3=vt3=F8dCufHAsmHvu%2BkXINA%3D&lg2=UtASsssmOIJ0bQ%3D%3D&id2=Uoe8iq7AMQYr9w%3D%3D&nk2=EuRW4ijvt3EzFqo%3D;lgc=qq420443292;t=4d2ab2f4f279727db2fdcb38403be928;sgcookie=E1004KyYpLVUxWvASqLqDBGGlO57%2BHVOtKE4QLtvkCy8q4DS6aVtAUw5bFc0BhHXaYbvWn%2FUf2FP1rMRruUhAkjaIg%3D%3D;uc4=id4=0%40UO%2B6bNeDfyBRdijL1kTvmXrP%2FeTf&nk4=0%40EJv1iv2d1lVgnhBc5HAxhs0CGxQoeg%3D%3D;tracknick=qq420443292;lc=VyT0b2FuYBsaZO9BQpn%2FlA%3D%3D;_cc_=V32FPkk%2Fhw%3D%3D;lid=qq420443292;mt=ci=3_1;thw=cn;_m_h5_tk=7370fc9cdb550523a8d1706097e61cd0_1602565458540;_m_h5_tk_enc=fff69fe72c0931dc4743e292517bd55b;l=eBNDAWbrOKJMY39zBOfwourza77OSdRAguPzaNbMiOCPskfe59YGWZ55pITwC3GOh6k9R3WiRcn6BeYBq5AQUGiZIosM_Ckmn;tfstk=cWiABNxH_QAcEXe-LqLlfjfxptoAauATNtNGXpgG7bMJGeIOfsUyp9IK7yRIPWf..;isg=BNfX9hqdRKtsbMAnABUcQVDpbUshHKt-Y1Yo9CkE76YNWPeaMeyRzpHyvvLGsIP2; t=4d2ab2f4f279727db2fdcb38403be928; unb=1680549126; uc1=cookie21=V32FPkk%2FhoymTKMFRRsLIQ%3D%3D&cookie16=W5iHLLyFPlMGbLDwA%2BdvAGZqLg%3D%3D&cookie15=URm48syIIVrSKA%3D%3D&cookie14=Uoe0b0C7b55TSw%3D%3D&pas=0&existShop=true; uc3=vt3=F8dCufHAsmDIyXWBUGs%3D&id2=Uoe8iq7AMQYr9w%3D%3D&nk2=EuRW4ijvt3EzFqo%3D&lg2=Vq8l%2BKCLz3%2F65A%3D%3D; csg=6572e333; lgc=qq420443292; cookie17=Uoe8iq7AMQYr9w%3D%3D; sgcookie=E100WbF%2BJbfbZ47dUWGEBnxiD%2BjLvJbEYzZpQ0StldOf7dRKnE%2F2hLWO9pgYZGsZn%2FVm4C8c%2BXFK069XRautoFHMrg%3D%3D; dnk=qq420443292; skt=88c4b198f4ae1e93; cookie2=22895d59c7ae9f573da9081631af259a; existShop=MTYwMjU1OTI2Mw%3D%3D; uc4=nk4=0%40EJv1iv2d1lVgnhBc5HAxhs0CGlpnyg%3D%3D&id4=0%40UO%2B6bNeDfyBRdijL1kTvmXrOuMeL; tracknick=qq420443292; lc=VyT0b2FuYBsaZO9BQpn%2FlA%3D%3D; _cc_=UIHiLt3xSw%3D%3D; lid=qq420443292; _l_g_=Ug%3D%3D; sg=267; _nk_=qq420443292; cookie1=Vqt4wTL0bvXhUv6zXb2%2BKhH3c3sMXtMv3s26xzUFbH8%3D; _tb_token_=e3beebabdb531")
				.addHeader("Accept-Encoding", "gzip,deflate")
				.addHeader("Postman-Token", "ebf1b2ed-1e4c-4df8-89cf-dddb0c9f2b9e").build();
		try {
			Response response = client.newCall(request).execute();
			response.headers();
			response.networkResponse();
			response.message();
			System.out.println(response.headers());
			System.out.println(response.networkResponse());
			System.out.println(response.message());
			System.out.println(response.body());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
