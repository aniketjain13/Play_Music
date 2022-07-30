package com.aniketjain.playmusic

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aniketjain.playmusic.databinding.ActivityFeedbackBinding
import java.util.*
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

@Suppress("DEPRECATION")
class FeedbackActivity : AppCompatActivity() {

    //BINDING DECLARATION
    lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //SETTING THEME OF THE ACTIVITY
        setTheme(MainActivity.currentThemeNav[MainActivity.themeindex])

        //BINDING INITIALIZATION
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Feedback"

        binding.buttonfb.setOnClickListener {
            //MAILING TO THE EDITOR OF THE APP
            val feedbackMsg = binding.feedbackfb.text.toString() + "\n" + binding.emailfb.text.toString()
            val subject = binding.topicfb.text.toString()
            val userName = "aniketjain836@gmail.com"
            val password = "welcometoplaymusic"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(feedbackMsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)){
              thread {
                  try {
                      val properties = Properties()
                      properties["mail.smtp.auth"] = true
                      properties["mail.smtp.starttls.enable"] = true
                      properties["mail.smtp.host"] = "smtp.gmail.com"
                      properties["mail.smtp.port"] = "587"
                      val session = javax.mail.Session.getInstance(
                          properties,
                          object : javax.mail.Authenticator() {
                              override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                                  return javax.mail.PasswordAuthentication(userName, password)
                              }
                          })
                      val mail = MimeMessage(session)
                      mail.subject = subject
                      mail.setText(feedbackMsg)
                      mail.setFrom(InternetAddress(userName))
                      mail.setRecipients(
                          javax.mail.Message.RecipientType.TO,
                          InternetAddress.parse(userName))
                      Transport.send(mail)

                  } catch (e: Exception) {
                      Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                  }
              }.start()
              Toast.makeText(this, "Thanks for the feedback!!", Toast.LENGTH_SHORT).show()
              finish()
            }
            else{
                Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show()
            }
        }

    }
}