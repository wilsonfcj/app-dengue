package aluno.ifsc.app.focos.dengue

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class DengueAplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(BuildConfig.VERSION_CODE.toLong())
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

}