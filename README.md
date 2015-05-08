## Beam Configuration Service

BCS is a configuration management solution based on ZooKeeper. It provides a client with convenient APIs to read data and subscribe modification from ZooKeeper.

### Requirements

Beam common framework needs to be installed.

See https://github.com/gaofeihang/beam-framework

### Usage

#### Installation

    git clone https://github.com/gaofeihang/beam-cs.git
    cd beam-cs
    mvn clean install -Dmaven.test.skip

#### Maven Dependency

    <dependency>
        <groupId>net.beamlight</groupId>
        <artifactId>bcs-client</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
#### API

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

#### ZooKeeper Connection

Add beam-cs.properties to your classpath, e.g.

    beam.cs.zkAddr=localhost:2181
    
Or use system property

    -Dbeam.cs.zkAddr=localhost:2181
