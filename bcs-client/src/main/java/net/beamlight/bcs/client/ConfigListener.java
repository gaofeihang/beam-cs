package net.beamlight.bcs.client;

import java.util.List;

/**
 * Created on May 6, 2015
 *
 * @author gaofeihang
 * @since 1.0.0
 */
public interface ConfigListener {
    
    void dataChanged(String path, String data);
    
    void childChanged(String path, List<String> children);

}
