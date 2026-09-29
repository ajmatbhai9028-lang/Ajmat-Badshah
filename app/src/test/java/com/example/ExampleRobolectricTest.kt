package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.InteriorStyle
import com.example.data.model.UserStyleProfile
import com.example.data.repository.WciRepository
import com.example.ui.viewmodel.WciViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WCI Furniture", appName)
    }

    @Test
    fun `verify catalog products loaded with specifications`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = WciRepository(context)

        assertTrue(repository.catalogProducts.isNotEmpty())
        val sofa = repository.getProductById("WCI-SOFA-01")
        assertNotNull(sofa)
        assertEquals("København Modular 3-Seater Sofa", sofa!!.name)
        assertTrue(sofa.specifications.isNotEmpty())
        assertTrue(sofa.sustainabilityScore > 90)
        assertTrue(sofa.styles.contains(InteriorStyle.MODERN))
    }

    @Test
    fun `verify shoppable moodboards and reviews`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = WciRepository(context)

        assertTrue(repository.moodboards.isNotEmpty())
        assertTrue(repository.verifiedReviews.isNotEmpty())
        val firstMoodboard = repository.moodboards.first()
        assertTrue(firstMoodboard.bundledProductIds.isNotEmpty())
    }

    @Test
    fun `verify interior design styles representation and preferences persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = WciRepository(context)

        // All 4 key styles must exist
        val styles = InteriorStyle.values().map { it.name }
        assertTrue(styles.contains("MODERN"))
        assertTrue(styles.contains("BOHEMIAN"))
        assertTrue(styles.contains("TRADITIONAL"))
        assertTrue(styles.contains("INDUSTRIAL"))

        // Create and save test profile
        val profile = UserStyleProfile(
            primaryStyle = InteriorStyle.BOHEMIAN,
            secondaryStyle = InteriorStyle.MODERN,
            styleScores = mapOf(InteriorStyle.BOHEMIAN to 60, InteriorStyle.MODERN to 40),
            preferredMaterials = listOf("Rattan", "Teak", "Linen"),
            preferredPalette = "Sun-Baked Earth & Terracotta",
            spaceGoal = "Soulful & Collected Warmth"
        )
        repository.saveUserStyleProfile(profile)

        val retrieved = repository.getUserStyleProfile()
        assertNotNull(retrieved)
        assertEquals(InteriorStyle.BOHEMIAN, retrieved!!.primaryStyle)
        assertEquals(InteriorStyle.MODERN, retrieved.secondaryStyle)
        assertEquals(60, retrieved.styleScores[InteriorStyle.BOHEMIAN])
    }

    @Test
    fun `verify personalized recommendation ordering matches user profile style`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = WciViewModel(app)

        val profile = UserStyleProfile(
            primaryStyle = InteriorStyle.BOHEMIAN,
            secondaryStyle = InteriorStyle.MODERN,
            styleScores = mapOf(InteriorStyle.BOHEMIAN to 80, InteriorStyle.MODERN to 20)
        )
        viewModel.saveStyleProfile(profile)

        val recommended = viewModel.getPersonalizedRecommendations()
        assertTrue(recommended.isNotEmpty())
        // Top product should have Bohemian style
        assertTrue(recommended.first().styles.contains(InteriorStyle.BOHEMIAN))

        // Check personalized moodboards prioritizing Bohemian
        val moodboards = viewModel.getPersonalizedMoodboards()
        assertTrue(moodboards.isNotEmpty())
        assertEquals(InteriorStyle.BOHEMIAN, moodboards.first().matchingStyle)
    }

    @Test
    fun `verify call and whatsapp contact intents`() {
        val callIntent = android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:7320054330"))
        assertEquals("tel:7320054330", callIntent.dataString)

        val waIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://wa.me/919572349911"))
        assertTrue(waIntent.dataString!!.contains("9572349911"))
    }

    @Test
    fun `verify SupportFooter helper functions generate correct intents`() {
        val callIntent = com.example.ui.components.createSupportCallIntent("7320054330")
        assertEquals(android.content.Intent.ACTION_DIAL, callIntent.action)
        assertEquals("tel:7320054330", callIntent.dataString)

        val waIntent = com.example.ui.components.createSupportWhatsAppIntent("9572349911")
        assertEquals(android.content.Intent.ACTION_VIEW, waIntent.action)
        assertEquals("https://wa.me/9572349911", waIntent.dataString)
    }

    @Test
    fun `verify style quiz progress bar calculations across multi-step quiz`() {
        val totalQuestions = 5

        // Step 1 (index 0)
        assertEquals(0.2f, com.example.ui.screens.calculateQuizProgress(0, totalQuestions), 0.001f)
        assertEquals(20, com.example.ui.screens.calculateQuizPercentage(0, totalQuestions))

        // Step 2 (index 1)
        assertEquals(0.4f, com.example.ui.screens.calculateQuizProgress(1, totalQuestions), 0.001f)
        assertEquals(40, com.example.ui.screens.calculateQuizPercentage(1, totalQuestions))

        // Step 3 (index 2)
        assertEquals(0.6f, com.example.ui.screens.calculateQuizProgress(2, totalQuestions), 0.001f)
        assertEquals(60, com.example.ui.screens.calculateQuizPercentage(2, totalQuestions))

        // Step 4 (index 3)
        assertEquals(0.8f, com.example.ui.screens.calculateQuizProgress(3, totalQuestions), 0.001f)
        assertEquals(80, com.example.ui.screens.calculateQuizPercentage(3, totalQuestions))

        // Step 5 (index 4 - final question)
        assertEquals(1.0f, com.example.ui.screens.calculateQuizProgress(4, totalQuestions), 0.001f)
        assertEquals(100, com.example.ui.screens.calculateQuizPercentage(4, totalQuestions))
    }
}
