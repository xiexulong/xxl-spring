package com.xxl;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class XxlDemo {


    public static void main(String[] args) throws Exception {
        test2(args);
//        test3(args);
    }

    public static void test3(String[] args) throws Exception{
        Settings settings = Settings.builder()
                //设置client.transport.sniff为true来使客户端去嗅探整个集群的状态
//                .put("client.transport.sniff", false)
                .put("cluster.name", "xxl").build();


        //2) 创建一个客户端client对象
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //3) 使用client对象创建一个索引库
        client.admin().indices().prepareCreate("index_hello")
                //执行操作
                .get();
        //4) 关闭client对象
        client.close();


    }
    public static void test2(String[] args) throws Exception{
        Settings settings = Settings.builder().put("cluster.name", "xxl").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("_all");
        System.out.println(searchRequestBuilder.get());
        System.out.println(client.connectedNodes());
    }


    protected TransportClient buildClient() {
        String clusterNodes = "127.0.0.1:9300";
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", "test")
                    .build();
            PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(settings);
            if (!"".equals(clusterNodes)) {
                for (String nodes : clusterNodes.split(",")) {
                    String[] inetSocket = nodes.split(":");
                    String address = inetSocket[0];
                    Integer port = Integer.valueOf(inetSocket[1]);
                    preBuiltTransportClient.addTransportAddress(new
                            TransportAddress(InetAddress.getByName(address), port));
                }
                client = preBuiltTransportClient;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }
}
