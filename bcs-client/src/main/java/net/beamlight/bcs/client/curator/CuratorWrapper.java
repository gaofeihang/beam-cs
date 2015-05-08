package net.beamlight.bcs.client.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;

/**
 * Created on May 6, 2015
 *
 * @author gaofeihang
 * @since 1.0.0
 */
public class CuratorWrapper {
    
    private CuratorFramework client;
    
    public CuratorWrapper(String zkAddr) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkAddr)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }
    
    public boolean checkExists(String path, Watcher watcher) throws Exception {
        if (watcher == null) {
            return client.checkExists().forPath(path) != null;
        }
        return client.checkExists().usingWatcher(watcher).forPath(path) != null;
    }
    
    public byte[] getData(String path, Watcher watcher) throws Exception {
        if (watcher == null) {
            return client.getData().forPath(path);
        }
        return client.getData().usingWatcher(watcher).forPath(path);
    }
    
    public List<String> getChildren(String path, Watcher watcher) throws Exception {
        if (watcher == null) {
            return client.getChildren().forPath(path);
        }
        return client.getChildren().usingWatcher(watcher).forPath(path);
    }
    
}
