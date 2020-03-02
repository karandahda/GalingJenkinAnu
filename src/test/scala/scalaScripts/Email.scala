package scalaScripts
import org.apache.commons.mail._



  package object Email {

    implicit def stringToSeq(single: String): Seq[String] = Seq(single)
    implicit def liftToOption[T](t: T): Option[T] = Some(t)

    sealed abstract class MailType
    case object Plain extends MailType
    case object Rich extends MailType
    case object MultiPart extends MailType

    case class Mail(
                     from: (String, String), // (email -> name)
                     to: Seq[String],
                     cc: Seq[String] = Seq.empty,
                     bcc: Seq[String] = Seq.empty,
                     subject: String,
                     message: String,
                     richMessage: Option[String] = None,
                     attachments: Seq[(java.io.File)] = Seq.empty
                   )

    object send {
      def a(mail: Mail) {


        val format =
          if (mail.attachments.nonEmpty) MultiPart
          else if (mail.richMessage.isDefined) Rich
          else Plain

        val commonsMail: Email = format match {
          case Plain => new SimpleEmail().setMsg(mail.message)
          case Rich => new HtmlEmail().setHtmlMsg(mail.richMessage.get).setTextMsg(mail.message)
          case MultiPart => {
            val multipartEmail = new MultiPartEmail()
            mail.attachments.foreach { file =>
              val attachment = new EmailAttachment()
              attachment.setPath(file.getAbsolutePath)
              attachment.setDisposition(EmailAttachment.ATTACHMENT)
              attachment.setName(file.getName)
              multipartEmail.attach(attachment)
            }
            multipartEmail.setMsg(mail.message)
          }
        }

        // TODO Set authentication from your configuration, sys properties or w/e

        // Can't add these via fluent API because it produces exceptions
        mail.to foreach (commonsMail.addTo("kdahda@gmail.com"))
        //mail.cc foreach (commonsMail.addCc(_))
        //mail.bcc foreach (commonsMail.addBcc(_))

        // gmail config
        commonsMail.setHostName("smtp.googlemail.com")
        commonsMail.setAuthentication("email","pass")
        commonsMail.setSSLOnConnect(true)
        commonsMail.setSmtpPort(465)

        commonsMail.
          setFrom(mail.from._1, mail.from._2).
          setSubject(mail.subject).
          send()
      }
    }
  }
