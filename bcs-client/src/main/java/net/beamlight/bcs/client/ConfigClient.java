package net.beamlight.bcs.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.beamlight.bcs.client.curator.CuratorWrapper;
import net.beamlight.commons.config.PropertyConfig;
import net.beamlight.core.serialize.StringEncoder;

import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on May 6, 2015
 *
 * @author gaofeihang
 * @since 1.0.0
 */
public class ConfigClient implements BeamConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigClient.class);
    
    private static final String ZK_ADDR = PropertyConfig.getInstance(Constants.PROPERTY_FILE)
            .getProperty(Constants.KEY_ZK_ADDR);
    
    private static Map<String, BeamConfig> instances = new HashMap<String, BeamConfig>();
    
    private CuratorWrapper curator;
    
    private ConfigClient(String zkAddr) {
        curator = new CuratorWrapper(zkAddr);
    }
    
    public static BeamConfig getInstance() {
        return getInstance(ZK_ADDR);
    }
    
    public static synchronized BeamConfig getInstance(String zkAddr) {
        BeamConfig client = instances.get(zkAddr);
        if (client == null) {
            client = new ConfigClient(zkAddr);
            instances.put(zkAddr, client);
        }
        return client;
    }

    @Override
    public String getData(String path) {
        return getData(path, null);
    }
    
    private String getData(String path, Watcher watcher) {
        try {
            byte[] bytes = curator.getData(path, watcher);
            return StringEncoder.decode(bytes);
        } catch (NoNodeException nne) {
            try {
                curator.checkExists(path, watcher);
            } catch (Exception e) {
                LOG.error("Check exists error: {}", path, e);
            }
        } catch (Exception e) {
            LOG.error("Get data error: {}", path, e);
        }
        return null;
    }

    @Override
    public List<String> getChildren(String path) {
        return getChildren(path, null);
    }
    
    private List<String> getChildren(String path, Watcher watcher) {
        try {
            return curator.getChildren(path, watcher);
        } catch (NoNodeException nne) {
            try {
                curator.checkExists(path, watcher);
            } catch (Exception e) {
                LOG.error("Check exists error: {}", path, e);
            }
        } catch (Exception e) {
            LOG.error("Get children error: {}", path, e);
        }
        return null;
    }

    @Override
    public void setListener(String path, final ConfigListener listener) {
        Watcher watcher = new Watcher() {
            
            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                
                if (event.getType() == EventType.NodeCreated) {
                    String data = getData(path, this);
                    getChildren(path, this);
                    listener.dataChanged(event.getPath(), data);
                    
                } else if (event.getType() == EventType.NodeDataChanged) {
                    String data = getData(path, this);
                    listener.dataChanged(event.getPath(), data);
                    
                } else if (event.getType() == EventType.NodeDeleted) {
                    getData(path, this);
                    listener.dataChanged(event.getPath(), null);
                    
                } else if (event.getType() == EventType.NodeChildrenChanged) {
                    List<String> children = getChildren(path, this);
                    listener.childChanged(path, children);
                }
            }
        };
        
        if (getData(path, watcher) != null) {
            // when node exists
            getChildren(path, watcher);
        }
    }

}
