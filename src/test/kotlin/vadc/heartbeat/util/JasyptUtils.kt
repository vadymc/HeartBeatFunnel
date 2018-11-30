package vadc.heartbeat.util

import org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI
import org.junit.Ignore
import org.junit.Test
import java.util.*

private val appName = "HeartBeatFunnel"

private val password = "mysecurepass"

class JasyptUtils {

    @Ignore
    @Test
    fun generate() {
        val value = UUID.randomUUID().toString().replace("-", "")
        JasyptPBEStringEncryptionCLI.main(arrayOf(appName, "password=$password", "input=$value", "algorithm=PBEWithMD5AndDES"))
    }

}
