package ai.openclaw.android.node

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandSafetyPolicyTest {
  @Test
  fun `destructive command requires explicit confirmation`() {
    assertTrue(CommandSafetyPolicy.needsDestructiveConfirmation("sms.send", "{}"))
    assertFalse(CommandSafetyPolicy.needsDestructiveConfirmation("sms.send", "{\"confirm\":true}"))
  }

  @Test
  fun `non destructive command does not require confirmation`() {
    assertFalse(CommandSafetyPolicy.needsDestructiveConfirmation("device.info", "{}"))
  }

  @Test
  fun `https hostname is allowed`() {
    assertEquals(LinkSafetyDecision.Allow, CommandSafetyPolicy.classifyLink("https://docs.openclaw.ai"))
  }

  @Test
  fun `http links require confirmation`() {
    val decision = CommandSafetyPolicy.classifyLink("http://example.com")
    assertTrue(decision is LinkSafetyDecision.RequiresConfirmation)
  }

  @Test
  fun `unsupported scheme is blocked`() {
    val decision = CommandSafetyPolicy.classifyLink("javascript:alert(1)")
    assertTrue(decision is LinkSafetyDecision.Blocked)
  }

  @Test
  fun `ip destination requires confirmation`() {
    val decision = CommandSafetyPolicy.classifyLink("https://10.0.2.2/ui")
    assertTrue(decision is LinkSafetyDecision.RequiresConfirmation)
  }
}
