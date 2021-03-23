package utils;

public class getWebPath {
    public static String getWebPaths(String fileName) {
        // file:/D:/JavaWeb/.metadata/.me_tcat/webapps/TestBeanUtils/WEB-INF/classes/
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        // ȥ������·���и��ֲ���Ҫ�Ķ���
        path = path.replace('/', '\\'); // ��/����\
        path = path.replace("file:", ""); // ȥ��file:
        // path = path.replace("classes\\", ""); // ȥ��class\
        path = path.substring(1); // ȥ����һ��\,�� \D:\JavaWeb...
 
        // ������ļ���������·���ϼ����ļ���
        if(fileName!=null) {
            if (fileName.isEmpty() == false) {
                path += fileName;
            }
        }
        return path.replace("%20", " ").replace("\\classes", "");
    }
}
