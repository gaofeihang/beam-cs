package net.beamlight.bcs.client;

import java.util.List;

/**
 * Created on May 5, 2015
 *
 * @author gaofeihang
 * @since 1.0.0
 */
public interface BeamConfig {
    
    String getData(String path);
    
    List<String> getChildren(String path);
    
    void setListener(String path, ConfigListener listener);

}
