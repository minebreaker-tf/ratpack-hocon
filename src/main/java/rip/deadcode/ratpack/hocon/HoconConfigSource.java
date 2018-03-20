package rip.deadcode.ratpack.hocon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.*;
import ratpack.api.Nullable;
import ratpack.config.ConfigSource;
import ratpack.file.FileSystemBinding;

import java.util.Map;

public final class HoconConfigSource implements ConfigSource {

    @Nullable
    private final Config config;

    public HoconConfigSource() {
        this.config = null;
    }

    public HoconConfigSource( Config config ) {
        this.config = config;
    }

    @Override
    public ObjectNode loadConfigData(
            ObjectMapper objectMapper, FileSystemBinding fileSystemBinding ) throws Exception {

        Config config = this.config == null ? ConfigFactory.load() : this.config;
        Config ratpackConfig = config.getConfig( "ratpack" );

        return walk( ratpackConfig.root(), objectMapper );
    }

    private static ObjectNode walk( ConfigObject config, ObjectMapper objectMapper ) {
        ObjectNode node = objectMapper.createObjectNode();

        for ( Map.Entry<String, ConfigValue> entry : config.entrySet() ) {

            switch ( entry.getValue().valueType() ) {
            case OBJECT:
                node.set( entry.getKey(), walk( (ConfigObject) entry.getValue(), objectMapper ) );
                break;
            case LIST:
                node.set( entry.getKey(), walkList( (ConfigList) entry.getValue(), objectMapper ) );
                break;
            case NUMBER:
                Object number = entry.getValue().unwrapped();
                if ( number instanceof Float || number instanceof Double ) {
                    node.put( entry.getKey(), ( (Number) number ).doubleValue() );
                } else {
                    node.put( entry.getKey(), ( (Number) number ).longValue() );
                }
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

    private static ArrayNode walkList( ConfigList config, ObjectMapper objectMapper ) {

        ArrayNode arrayNode = objectMapper.createArrayNode();

        for ( ConfigValue each : config ) {
            switch ( each.valueType() ) {
            case OBJECT:
                arrayNode.add( walk( (ConfigObject) each, objectMapper ) );
                break;
            case LIST:
                arrayNode.add( walkList( (ConfigList) each, objectMapper ) );
                break;
            case NUMBER:
                Number number = (Number) each.unwrapped();
                if ( number instanceof Float || number instanceof Double ) {
                    arrayNode.add( ( number ).doubleValue() );
                } else {
                    arrayNode.add( ( number ).longValue() );
                }
                break;
            case BOOLEAN:
                arrayNode.add( (Boolean) each.unwrapped() );
                break;
            case NULL:
                arrayNode.addNull();
                break;
            case STRING:
                arrayNode.add( (String) each.unwrapped() );
                break;
            }
        }

        return arrayNode;
    }

}
