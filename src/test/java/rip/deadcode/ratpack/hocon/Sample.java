package rip.deadcode.ratpack.hocon;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.jackson.Jackson;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.test.embed.EmbeddedApp;

import static com.google.common.truth.Truth.assertThat;

public class Sample {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static final class TestConfig {
        private int id;
        private String name;
        private InnerConfig inner;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        private static final class InnerConfig {
            private Boolean value;
        }
    }

    @Test
    public void test() throws Exception {

        Action<Chain> handlers = chain -> {
            chain.get( "", ctx -> ctx.render( Jackson.json( ctx.get( TestConfig.class ) ) ) );
        };

        RatpackServer server = RatpackServer.of( spec -> {

            ServerConfig serverConfig = ServerConfig.builder()
                                                    .add( new HoconConfigSource() )
                                                    .require( "/test", TestConfig.class )
                                                    .build();

            assertThat( serverConfig.isDevelopment() ).isTrue();
            assertThat( serverConfig.getPort() ).isEqualTo( 8000 );

            spec.serverConfig( serverConfig )
                .handlers( handlers );
        } );

        EmbeddedApp.fromServer( server ).test( client -> {

            TestConfig result = new Gson().fromJson( client.get().getBody().getText(), TestConfig.class );

            assertThat( result.getId() ).isEqualTo( 123 );
            assertThat( result.getName() ).isEqualTo( "hoge" );
            assertThat( result.getInner().getValue() ).isTrue();
        } );

    }

}