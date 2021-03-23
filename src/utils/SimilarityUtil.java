package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;

public class SimilarityUtil {

	static {

		CustomDictionary.add("����");

		CustomDictionary.add("����");

	}

	private SimilarityUtil() {

	}

	/**
	 * 
	 * ����������ӵ����ƶ�
	 *
	 * 
	 * 
	 * @param sentence1
	 * 
	 * @param sentence2
	 * 
	 * @return
	 * 
	 */

	public static Float getSimilarity(String sentence1, String sentence2) {

		List<String> sent1Words = getSplitWords(sentence1);

//		System.out.println(sent1Words);

		List<String> sent2Words = getSplitWords(sentence2);

//		System.out.println(sent2Words);

		List<String> allWords = mergeList(sent1Words, sent2Words);

		int[] statistic1 = statistic(allWords, sent1Words);

		int[] statistic2 = statistic(allWords, sent2Words);

		double dividend = 0;

		double divisor1 = 0;

		double divisor2 = 0;

		for (int i = 0; i < statistic1.length; i++) {

			dividend += statistic1[i] * statistic2[i];

			divisor1 += Math.pow(statistic1[i], 2);

			divisor2 += Math.pow(statistic2[i], 2);

		}

		return (float) (dividend / (Math.sqrt(divisor1) * Math.sqrt(divisor2)));

	}

	private static int[] statistic(List<String> allWords, List<String> sentWords) {

		int[] result = new int[allWords.size()];

		for (int i = 0; i < allWords.size(); i++) {

			result[i] = Collections.frequency(sentWords, allWords.get(i));

		}

		return result;

	}

	private static List<String> mergeList(List<String> list1, List<String> list2) {

		List<String> result = new ArrayList<>();

		result.addAll(list1);

		result.addAll(list2);

		return result.stream().distinct().collect(Collectors.toList());

	}

	private static List<String> getSplitWords(String sentence) {

		// ȥ����html��ǩ

		sentence = Jsoup.parse(sentence.replace(" ", "")).body().text();

		// �����Żᱻ������Ϊһ��Term��ȥ��֮

		return HanLP.segment(sentence).stream().map(a -> a.word)
				.filter(s -> !"`~!@#$^&*()=|{}':;',\\[\\].<>/?~��@#������&*��������|{}��������������'�������� ".contains(s))
				.collect(Collectors.toList());

	}

	public static float imgsimilar(String imga, String imgb) {
		File img0 = new File(imga);
		File img1 = new File(imgb);
		return imgsimilar(img0,img1);
	}
	
	public static float imgsimilar(File imga, File imgb) {
		FingerPrint fp1 = new FingerPrint(FingerPrint.readImage(imga));
		FingerPrint fp2 = new FingerPrint(FingerPrint.readImage(imgb));
		Float resfirst = fp1.compare(fp2);
		Float ressecond = PhotoDigest.compare(PhotoDigest.getData(imga), PhotoDigest.getData(imgb));
		Float sub = resfirst - ressecond;
		Float avg = (resfirst + ressecond) / 2;
		float res = 0;
		if (sub > 0.25) {
			res = avg + sub;
			if (sub == 0) {
				res = 1;
			}
			if (res > 1) {

				Float s = Float.parseFloat("0.0" + Float.toString(avg).substring(2, Float.toString(avg).length() - 1));
				res = (float) (s + 0.9);
			}
		} else {
			res = avg;
		}
		imga.delete();  
		imgb.delete();  
		return res;
	}

	public static File readImage(String path) {
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
//			float fileLength = httpURLConnection.getContentLength();
//			System.out.println("��Ҫ���ص��ļ���СΪ:" + fileLength / (1024) + "KB");
			URLConnection con = url.openConnection();
			con.connect();
			BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
			String name = path.substring(path.lastIndexOf("/"), path.length()).replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5��,��������]+","");
			if(name.endsWith(".jpg")||name.endsWith(".jpeg")||name.endsWith(".png")) {
				name = path.substring(path.lastIndexOf("/")+1, path.length());
			}else {
				name = path.substring(path.lastIndexOf("/")+1, path.length()) + ".jpg";
			}
			dir = getWebPath.getWebPaths(null) + name;
			file = new File(dir);
			OutputStream out = new FileOutputStream(file);
			int size = 0;
//			int len = 0;
			byte[] buf = new byte[2048];
			while ((size = bin.read(buf)) != -1) {
//				len += size;
				out.write(buf, 0, size);
//				System.out.println("������-------> " + len * 100 / fileLength + "%\n");
			}
			bin.close();
			out.close();
//			System.out.println("�ļ����سɹ���");
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
		return file;
	}
}