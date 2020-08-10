package com.mengli.app;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

public class HelloGraph {
    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir","D:/software/hadoop");
        JanusGraph janusGraph = JanusGraphFactory.build()
                // 设置图链接属性
                .set("graph.set-vertex-id", "false")
                .set("gremlin.graph", "org.janusgraph.core.JanusGraphFactory")
                // hbase
                .set("storage.backend", "hbase")
                .set("storage.hostname", "39.105.5.251")
                .set("storage.hbase.table", "mengliGraph")
                // cache
                .set("cache.db-cache", "true")
                .set("cache.db-cache-clean-wait", "20")
                .set("cache.db-cache-time", "180000")
                .set("cache.db-cache-size", "0.5")
                // search
                .set("index.search.backend", "elasticsearch")
                .set("index.search.hostname", "39.105.5.251")
                .set("index.search.port", "9200")
                .set("index.search.elasticsearch.client-only", "true")
                .set("index.search.elasticsearch.local-mode", "true").open();
        // 测试是否正常打开
        System.out.println("Graph Open state : " + janusGraph.isOpen());
        // 最终还是没有测试成功,服务器的内存比较小,完全启动之后,内存溢出.
    }
}
