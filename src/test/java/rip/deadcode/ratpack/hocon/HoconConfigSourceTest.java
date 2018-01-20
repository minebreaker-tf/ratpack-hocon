package rip.deadcode.ratpack.hocon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import ratpack.server.ServerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class HoconConfigSourceTest {

    private static ServerConfig create( Map<String, Object> params ) {
        Config config = ConfigFactory.parseMap( params );
        return ServerConfig.builder().add( new HoconConfigSource( config ) ).build();
    }

    @SuppressWarnings( "ConstantConditions" )
    @Test
    public void test() {

        Map<String, Object> params = ImmutableMap.of(
                "ratpack", ImmutableMap.of(
                        "server", ImmutableMap.of(
                                "port", 8080,
                                "development", true,
                                "maxChunkSize", "1000",
                                "maxContentLength", "500",
                                "connectTimeoutMillis", "10000"
                        ) ) );
        ServerConfig result = create( params );

        assertThat( result.getPort() ).isEqualTo( 8080 );
        assertThat( result.isDevelopment() ).isTrue();
        assertThat( result.getMaxChunkSize() ).isEqualTo( 1000 );
        assertThat( result.getMaxContentLength() ).isEqualTo( 500 );
        assertThat( result.getConnectTimeoutMillis().get() ).isEqualTo( 10000 );
    }

    @Test
    public void testNumber() {
        Map<String, Object> params = ImmutableMap.of( "ratpack.test", 123 );
        Integer result = create( params ).get( "/test", Integer.class );
        assertThat( result ).isEqualTo( 123 );
    }

    @Test
    public void testBoolean() {
        Map<String, Object> params = ImmutableMap.of( "ratpack.test", true );
        Boolean result = create( params ).get( "/test", Boolean.class );
        assertThat( result ).isEqualTo( true );
    }

    @Test
    public void testString() {
        Map<String, Object> params = ImmutableMap.of( "ratpack.test", "foo" );
        String result = create( params ).get( "/test", String.class );
        assertThat( result ).isEqualTo( "foo" );
    }

    @Test
    public void testNull() {
        Map<String, Object> params = new HashMap<>();
        params.put( "ratpack.test", null );
        Object result = create( params ).get( "/test", Object.class );
        assertThat( result ).isNull();
    }

    @Test
    public void testList() {
        Map<String, Object> params = ImmutableMap.of( "ratpack.test", ImmutableList.of( "foo", "bar" ) );
        List result = create( params ).get( "/test", List.class );
        assertThat( result ).containsExactly( "foo", "bar" );
    }

    @Test
    public void testListInList() {
        Map<String, Object> params = ImmutableMap.of(
                "ratpack.test", ImmutableList.of( ImmutableList.of( "hoge", "piyo" ), "bar" ) );
        List result = create( params ).get( "/test", List.class );
        assertThat( (List) result.get( 0 ) ).containsExactly( "hoge", "piyo" );
        assertThat( result.get( 1 ) ).isEqualTo( "bar" );
    }

    @Data
    private static final class Bean1 {
        private List<String> foo;
        private String bar;
    }

    @Test
    public void testListInMap() {

        Map<String, Object> params = ImmutableMap.of(
                "ratpack.test", ImmutableMap.of( "foo", ImmutableList.of( "hoge", "piyo" ),
                                                 "bar", "buz"
                ) );
        Bean1 result = create( params ).get( "/test", Bean1.class );
        assertThat( result.getFoo() ).containsExactly( "hoge", "piyo" );
        assertThat( result.getBar() ).isEqualTo( "buz" );
    }

    @Data
    @AllArgsConstructor
    private static final class Bean2 {
        private String foo;
    }

    @Test
    public void testMapInList() {
        Map<String, Object> params = ImmutableMap.of(
                "ratpack.test", ImmutableList.of( ImmutableMap.of( "foo", "bar" ), "buz" ) );
        List result = create( params ).get( "/test", List.class );

        assertThat( (Map) result.get( 0 ) ).containsExactly( "foo", "bar" );
        assertThat( result.get( 1 ) ).isEqualTo( "buz" );
    }

}