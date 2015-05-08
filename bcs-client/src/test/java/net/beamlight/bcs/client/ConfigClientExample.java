package net.beamlight.bcs.client;

import java.util.List;

import net.beamlight.commons.util.ThreadUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on May 6, 2015
 *
 * @author gaofeihang
 * @since 1.0.0
 */
public class ConfigClientExample {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigClientExample.class);
    private static final String ZK_PATH = "/beam/cs/test";
    private BeamConfig config = ConfigClient.getInstance();
    
    public void start() {
        String data = config.getData(ZK_PATH);
        LOG.info("Get data: {}, {}", ZK_PATH, data);
        
        List<String> children = config.getChildren(ZK_PATH);
        LOG.info("Get children: {}, {}", ZK_PATH, children);
        
        config.setListener(ZK_PATH, new ConfigListener() {
            @Override
            public void dataChanged(String path, String data) {
                LOG.info("Data changed: {}, {}", ZK_PATH, data);
            }
            @Override
            public void childChanged(String path, List<String> children) {
                LOG.info("Children changed: {}, {}", ZK_PATH, children);
            }
        });
        
        ThreadUtils.sleep(1000 * 600);
    }
    
    public static void main(String[] args) {
        new ConfigClientExample().start();
    }
}
