package ai.openclaw.android.node

import java.net.URI
import org.json.JSONObject

internal object CommandSafetyPolicy {
  private val destructiveCommands =
    setOf(
      "sms.send",
      "contacts.add",
      "calendar.add",
      "app.update",
    )

  fun needsDestructiveConfirmation(command: String, paramsJson: String?): Boolean {
    if (!destructiveCommands.contains(command)) {
      return false
    }
    return !isExplicitConfirm(paramsJson)
  }

  fun classifyLink(url: String): LinkSafetyDecision {
    val uri =
      try {
        URI(url)
      } catch (_: Throwable) {
        return LinkSafetyDecision.Blocked("invalid URL")
      }
    val scheme = uri.scheme?.lowercase() ?: return LinkSafetyDecision.Blocked("missing URL scheme")
    if (scheme != "https" && scheme != "http") {
      return LinkSafetyDecision.Blocked("unsupported URL scheme: $scheme")
    }

    val host = uri.host?.lowercase() ?: return LinkSafetyDecision.Blocked("missing URL host")
    if (host == "localhost" || host.endsWith(".local")) {
      return LinkSafetyDecision.RequiresConfirmation("local network destination")
    }
    if (host.isIpLiteral()) {
      return LinkSafetyDecision.RequiresConfirmation("IP address destination")
    }
    if (scheme == "http") {
      return LinkSafetyDecision.RequiresConfirmation("unencrypted HTTP destination")
    }
    return LinkSafetyDecision.Allow
  }

  fun isExplicitConfirm(paramsJson: String?): Boolean {
    if (paramsJson.isNullOrBlank()) {
      return false
    }
    return try {
      JSONObject(paramsJson).optBoolean("confirm", false)
    } catch (_: Throwable) {
      false
    }
  }
}

internal sealed interface LinkSafetyDecision {
  data object Allow : LinkSafetyDecision

  data class RequiresConfirmation(val reason: String) : LinkSafetyDecision

  data class Blocked(val reason: String) : LinkSafetyDecision
}

private fun String.isIpLiteral(): Boolean {
  return IPV4.matches(this) || IPV6.matches(this)
}

private val IPV4 = Regex("""^\d{1,3}(?:\.\d{1,3}){3}$""")
private val IPV6 = Regex("""^[0-9a-fA-F:]+$""")
