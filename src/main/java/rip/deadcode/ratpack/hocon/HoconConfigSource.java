package rip.deadcode.ratpack.hocon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.*;
import ratpack.config.ConfigSource;
import ratpack.file.FileSystemBinding;

import java.util.Map;

public final class HoconConfigSource implements ConfigSource {

    @Override
    public ObjectNode loadConfigData(
            ObjectMapper objectMapper, FileSystemBinding fileSystemBinding ) throws Exception {

        Config config = ConfigFactory.load();

        Config ratpackConfig = config.getConfig( "ratpack" );
//        config.entrySet().forEach( System.out::println );

        ObjectNode result = walk( ratpackConfig.root(), objectMapper );
        return result;
    }

    private static ObjectNode walk( ConfigObject config, ObjectMapper objectMapper ) {
        ObjectNode node = objectMapper.createObjectNode();

        for ( Map.Entry<String, ConfigValue> entry : config.entrySet() ) {

            ConfigValue value = entry.getValue();
            switch ( value.valueType() ) {
            case OBJECT:
                node.set( entry.getKey(), walk( (ConfigObject) entry.getValue(), objectMapper ) );
                break;
            case LIST:
                // TODO
//                node.set( entry.getKey(), walk( value, objectMapper ) );
                throw new UnsupportedOperationException( "not supported yet" );
            case NUMBER:
                node.put( entry.getKey(), ( (Number) entry.getValue().unwrapped() ).longValue() );
                break;
            case BOOLEAN:
                node.put( entry.getKey(), (Boolean) entry.getValue().unwrapped() );
                break;
            case NULL:
                node.putNull( entry.getKey() );
                break;
            case STRING:
                node.put( entry.getKey(), (String) entry.getValue().unwrapped() );
                break;
            }
        }

        return node;
    }
}
