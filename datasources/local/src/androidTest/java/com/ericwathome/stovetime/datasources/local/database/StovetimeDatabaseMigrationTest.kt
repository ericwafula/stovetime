package com.ericwathome.stovetime.datasources.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StovetimeDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), StovetimeDatabase::class.java)

    @Test
    fun cooks_survive_the_upgrade_to_version_2() {
        // given
        helper.createDatabase(DATABASE, 1).apply {
            execSQL(
                "INSERT INTO cookentity (dishId, kind, name, photo, startedAt, finishedAt, xp, rating, weeklyGoal) " +
                    "VALUES ('boiled-eggs', 'Lesson', 'Boiled eggs', 'lesson_boiled_eggs', 1000, 841000, 20, 'NailedIt', 4)",
            )
            close()
        }

        // when
        val migrated = helper.runMigrationsAndValidate(DATABASE, 2, true)

        // then
        migrated.query("SELECT dishId, xp, rating, weeklyGoal FROM cookentity").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(0)).isEqualTo("boiled-eggs")
            assertThat(cursor.getInt(1)).isEqualTo(20)
            assertThat(cursor.getString(2)).isEqualTo("NailedIt")
            assertThat(cursor.getInt(3)).isEqualTo(4)
        }
    }

    private companion object {
        const val DATABASE = "migration-test"
    }
}
