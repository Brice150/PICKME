// The application registers the swiper custom elements in `app.config.ts`, which is what brings
// their types into the build. The test compilation starts from the spec files instead, so it needs
// the declarations to be pulled in explicitly.
import 'swiper/element/bundle';
