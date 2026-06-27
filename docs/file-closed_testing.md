### Temporary release procedure while limited to closed testing

1. bump `app.version.code=`
2. Use this command so we sign with `release` instead of `debug`:

`KEYSTORE_PATH=~/keystores/fightingnerd/release.jks KEYSTORE_PASSWORD='<pw>' KEY_ALIAS='<alias>' KEY_PASSWORD='<keypw>' ./gradlew :composeApp:bundleRelease`