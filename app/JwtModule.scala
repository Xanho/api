import com.google.inject.AbstractModule
import pdi.jwt.JwtSession

class JwtModule extends AbstractModule {
  def configure() = {
    bind(classOf[JwtSession]).asEagerSingleton()
  }
}