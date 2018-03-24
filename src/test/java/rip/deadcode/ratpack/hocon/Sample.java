package rip.deadcode.ratpack.hocon;

import com.google.gson.Gson;
import org.junit.Test;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.jackson.Jackson;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.test.embed.EmbeddedApp;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class Sample {

    private static final class TestConfig {
        private int id;
        private String name;
        private InnerConfig inner;
        private List<String> list;

        private static final class InnerConfig {
            private Boolean value;

            public Boolean getValue() {
                return value;
            }

            public void setValue( Boolean value ) {
                this.value = value;
            }
        }

        public int getId() {
            return id;
        }

        public void setId( int id ) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public InnerConfig getInner() {
            return inner;
        }

        public void setInner( InnerConfig inner ) {
            this.inner = inner;
        }

        public List<String> getList() {
            return list;
        }

        public void setList( List<String> list ) {
            this.list = list;
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
            assertThat( result.getList() ).containsExactly( "foo", "bar" );
            assertThat( result.getInner().getValue() ).isTrue();
        } );

    }

}