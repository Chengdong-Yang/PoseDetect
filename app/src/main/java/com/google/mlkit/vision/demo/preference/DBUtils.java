package com.google.mlkit.vision.demo.preference;


import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

//数据库工具类：连接数据库用、获取数据库数据用
public class DBUtils {
    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动
    private static String user = "root";// 用户名
    private static String password = "12345678";// 密码

    private static Connection getConn(String dbname){
        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            String ip = "192.168.18.46"; // 写成本机地址，不能写成localhost
            // 尝试建立到给定数据库URL的连接
            connection = DriverManager.getConnection("jdbc:mysql://" + ip
                    + ":3306/" + dbname, user, password);
        }catch (Exception e){
            Log.i("DBUtils","Exception");
            e.printStackTrace();
        }
        return connection;
    }

    public static HashMap<String, Object> getAllInfo(){
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn("test");
        try {
            String sql = "select * from users;";
            if (connection != null){// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null){

                        while (rs.next()){
//                            String rsm = rs.getMetaData().getColumnName(1);
//                            Log.i("DBUtils","记录rsm ：" + rsm);
                            // 通过字段检索
                            String id  = rs.getString("nums");
                            String name = rs.getString("name");
                            String passw = rs.getString("passw");
                            int age  = rs.getInt("age");
                            String adress = rs.getString("adress");
                            Log.i("DBUtils","记录全 ：" + id + name + passw + age + adress);
                            map.put(id,","+name+","+passw+","+age+","+adress);
                        }
                        connection.close();
                        ps.close();
                        return  map;
                    }else {
                        Log.i("DBUtils","结果为空");
                        return null; }
                }else {
                    Log.i("DBUtils","sql");
                    return  null; }
            }else {
                Log.i("DBUtils","连接失败");
                return  null; }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DBUtils","异常：" + e.getMessage());
            return null;
        }

    }
    public static HashMap<String, Object> getInfoByName(String names){
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn("test");
        try {
            // mysql简单的查询语句。这里是根据users表的name字段来查询某条记录
            String sql = "select * from users where name = ? ;";
            if (connection != null){  // connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null){
                    // 设置上面的sql语句中的？的值为name
//                    ps.setString(1, names);
                    ps.setNString(1,names);
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
//                    rs.beforeFirst();
                    if (rs != null && rs.next()){
                        Log.i("DBUtils","记录 count ：" + rs.getMetaData().getColumnCount());
                        rs.previous();
                        while (rs.next()){
                            // 通过字段检索
                            String id  = rs.getString("nums");
                            String name = rs.getString("name");
                            String passw = rs.getString("passw");
                            int age  = rs.getInt("age");
                            String adress = rs.getString("adress");
                            Log.i("DBUtils","记录全 ：" + id + name + passw + age + adress);
                            map.put(id,","+name+","+passw+","+age+","+adress);
                        }
                        connection.close();
                        ps.close();
                        return  map;
                    }else {
                        Log.i("DBUtils","结果为空");
                        return null; }
                }else {
                    Log.i("DBUtils","sql");
                    return  null; }
            }else {
                Log.i("DBUtils","连接失败");
                return  null; }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DBUtils","异常：" + e.getMessage());
            return null;
        }

    }
}
