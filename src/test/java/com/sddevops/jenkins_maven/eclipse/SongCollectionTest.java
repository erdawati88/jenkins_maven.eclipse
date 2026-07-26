package com.sddevops.jenkins_maven.eclipse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SongCollectionTest {

    // ---------------------------------------------------------
    // Mock subclass (minimal change)
    // ---------------------------------------------------------
    static class MockSongCollection extends SongCollection {
        @Override
        protected String fetchSongJson() {
            return "{\"id\":\"001\",\"title\":\"Mock\",\"artiste\":\"X\",\"songLength\":3.0}";
        }
    }

    // ---------------------------------------------------------
    // Broken subclass to cover catch block
    // ---------------------------------------------------------
    static class BrokenSongCollection extends SongCollection {
        @Override
        protected String getApiUrl() {
            return "http://invalid-url";  // forces exception inside fetchSongJson()
        }
    }

    private SongCollection sc;
    private Song s1, s2, s3, s4;
    private final int SONG_COLLECTION_SIZE = 4;

    @BeforeEach
    void setUp() throws Exception {
        sc = new SongCollection();
        s1 = new Song("001", "good 4 u", "Olivia Rodrigo", 3.59);
        s2 = new Song("002", "Peaches", "Justin Bieber", 3.18);
        s3 = new Song("003", "MONTERO", "Lil Nas", 2.3);
        s4 = new Song("004", "bad guy", "billie eilish", 3.14);
        sc.addSong(s1);
        sc.addSong(s2);
        sc.addSong(s3);
        sc.addSong(s4);
    }

    @AfterEach
    void tearDown() throws Exception {
        // No teardown required because SongCollection has no external resources.
    }

    @Test
    void testGetSongs() {
        assertEquals(SONG_COLLECTION_SIZE +1, sc.getSongs().size());
    }

    @Test
    void testAddSong() {
        sc.addSong(new Song("005", "New Song", "New Artist", 3.00));
        assertEquals(SONG_COLLECTION_SIZE + 1, sc.getSongs().size());
    }

    @Test
    void testSortSongsByTitle() {
        sc.sortSongsByTitle();
        List<Song> sorted = sc.getSongs();

        assertEquals("MONTERO", sorted.get(0).getTitle());
        assertEquals("Peaches", sorted.get(1).getTitle());
        assertEquals("bad guy", sorted.get(2).getTitle());
        assertEquals("good 4 u", sorted.get(3).getTitle());
    }

    @Test
    void testSortSongsBySongLength() {
        List<Song> sorted = sc.sortSongsBySongLength();

        assertEquals(3.59, sorted.get(0).getSongLength());
        assertEquals(3.18, sorted.get(1).getSongLength());
        assertEquals(3.14, sorted.get(2).getSongLength());
        assertEquals(2.3, sorted.get(3).getSongLength());
    }

    @Test
    void testFindSongsById() {
        assertEquals("billie eilish", sc.findSongsById("004").getArtiste());
        assertNull(sc.findSongsById("193"));
    }

    @Test
    void testFindSongByTitle() {
        assertEquals("Lil Nas", sc.findSongByTitle("MONTERO").getArtiste());
        assertNull(sc.findSongByTitle("doesn't exist"));
    }

    // ORIGINAL 4 TESTS (kept intentionally for learning/tutorial)
    // These tests remain for educational clarity.
    // A parameterized test is added below to satisfy SonarQube duplication rules.

    @SuppressWarnings("java:S5976")
    @Test
    void testFetchSongOfTheDay_MockArtistNotAdded() {
        String mockJson = """
            {
                "id": "001",
                "title": "Mock Song",
                "artiste": "Mock Artist",
                "songLength": 4.25
            }
            """;

        SongCollection collection = spy(new SongCollection());
        doReturn(mockJson).when(collection).fetchSongJson();

        Song result = collection.fetchSongOfTheDay();

        assertNotNull(result);
        assertEquals("Mock Artist", result.getArtiste());
        assertEquals(0, collection.getSongs().size());
    }
    
    @SuppressWarnings("java:S5976")
    @Test
    void testFetchSongOfTheDay_TaylorSwiftAddedWithInitials() {
        String mockJson = """
            {
                "id": "010",
                "title": "Love Story",
                "artiste": "Taylor Swift",
                "songLength": 3.55
            }
            """;

        SongCollection collection = spy(new SongCollection());
        doReturn(mockJson).when(collection).fetchSongJson();

        Song result = collection.fetchSongOfTheDay();

        assertNotNull(result);
        assertEquals("TS", result.getArtiste());
        assertEquals(1, collection.getSongs().size());
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testFetchSongOfTheDay_BrunoMarsAddedWithInitials() {
        String mockJson = """
            {
                "id": "020",
                "title": "Grenade",
                "artiste": "Bruno Mars",
                "songLength": 3.42
            }
            """;

        SongCollection collection = spy(new SongCollection());
        doReturn(mockJson).when(collection).fetchSongJson();

        Song result = collection.fetchSongOfTheDay();

        assertNotNull(result);
        assertEquals("BM", result.getArtiste());
        assertEquals(1, collection.getSongs().size());
    }
    
    @SuppressWarnings("java:S5976")
    @Test
    void testFetchSongOfTheDay_OtherArtisteNotAdded() {
        String mockJson = """
            {
                "id": "030",
                "title": "Random Song",
                "artiste": "Some Other Artist",
                "songLength": 4.00
            }
            """;

        SongCollection collection = spy(new SongCollection());
        doReturn(mockJson).when(collection).fetchSongJson();

        Song result = collection.fetchSongOfTheDay();

        assertNotNull(result);
        assertEquals("Some Other Artist", result.getArtiste());
        assertEquals(0, collection.getSongs().size());
    }

    // ---------------------------------------------------------
    // NEW PARAMETERIZED TEST (fixes SonarQube duplication)
    // ---------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
        "001, Mock Song, Mock Artist, 4.25, Mock Artist, 0",
        "010, Love Story, Taylor Swift, 3.55, TS, 1",
        "020, Grenade, Bruno Mars, 3.42, BM, 1",
        "030, Random Song, Some Other Artist, 4.00, Some Other Artist, 0"
    })
    void testFetchSongOfTheDay_Parameterized(
            String id,
            String title,
            String artiste,
            double length,
            String expectedArtiste,
            int expectedAddedCount
    ) {

        String mockJson = String.format("""
            {
                "id": "%s",
                "title": "%s",
                "artiste": "%s",
                "songLength": %.2f
            }
            """, id, title, artiste, length);

        SongCollection collection = spy(new SongCollection());
        doReturn(mockJson).when(collection).fetchSongJson();

        Song result = collection.fetchSongOfTheDay();

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(expectedArtiste, result.getArtiste());
        assertEquals(length, result.getSongLength());

        assertEquals(expectedAddedCount, collection.getSongs().size());
        if (expectedAddedCount == 1) {
            assertTrue(collection.getSongs().contains(result));
        }
    }

    // ---------------------------------------------------------
    // Remaining tests
    // ---------------------------------------------------------

    @Test
    void testInvalidFetchSongOfTheDay() {
        SongCollection collection = spy(new SongCollection());
        doReturn(null).when(collection).fetchSongJson();

        assertNull(collection.fetchSongOfTheDay());
    }

    @Test
    void testExceptionHandlingInFetchSongOfTheDay() {
        SongCollection collection = spy(new SongCollection());
        doThrow(new RuntimeException("API failed")).when(collection).fetchSongJson();

        assertNull(collection.fetchSongOfTheDay());
    }

    @Test
    void testSongCollectionConstructorWithCapacity() {
        SongCollection localSc = new SongCollection(2);

        localSc.addSong(new Song("001", "A", "X", 3.0));
        localSc.addSong(new Song("002", "B", "Y", 3.0));
        localSc.addSong(new Song("003", "C", "Z", 3.0));

        assertEquals(2, localSc.getSongs().size());
    }

    @Test
    void testGetApiUrl() {
        SongCollection localSc = new SongCollection();
        assertNotNull(localSc.getApiUrl());
        assertTrue(localSc.getApiUrl().startsWith("https://"));
    }

    @Test
    void testFetchSongJsonException() {
        BrokenSongCollection broken = new BrokenSongCollection();
        assertNull(broken.fetchSongJson());
    }

    @Test
    void testGetYearOfSongCollection() {
        // Creating a pre-determined value in June 2024
        LocalDateTime mockDate = LocalDateTime.of(2024, Month.JUNE, 18, 16, 30);

        // LocalDateTime is a static class, hence we need to use mockStatic here
        MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

        // I want to mock the now() function of the LocalDateTime class
        // This means that later, when my program tries to run LocalDateTime.now(),
        // it will always give the mock date instead of today's actual date
        mocked.when(LocalDateTime::now).thenReturn(mockDate);

        Song song = new Song("1", "Eric", "Test Song", 3.45);
        SongCollection collection = new SongCollection();
        collection.addSong(song);

        assertEquals("2024", collection.getYearCreated());

        mocked.close();
    }
    
    @Test
    void testGetFullDateCreated() {
        LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);

        // LocalDateTime is a static class, hence we need to use mockStatic here
        MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

        // I want to mock the now() function of the LocalDateTime class
        // This means that later, when my program tries to run LocalDateTime.now(),
        // it will always give the mock date instead of today's actual date
        mocked.when(LocalDateTime::now).thenReturn(mockDate);

        SongCollection collection = new SongCollection();
        assertEquals("14/12/2025", collection.getFullDateCreated());

        mocked.close();
    }

    @Test
    void testBeforeDateCreatedComparison() {
        LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 12, 20, 30);
        LocalDateTime otherMockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);

        MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

        mocked.when(LocalDateTime::now).thenReturn(mockDate);
        SongCollection firstCollection = new SongCollection();

        mocked.when(LocalDateTime::now).thenReturn(otherMockDate);
        SongCollection secondCollection = new SongCollection();

        String result = "My collection is older!";
        assertEquals(firstCollection.compareCollection(secondCollection), result);

        mocked.close();
    }

    @Test
    void testAfterDateCreatedComparison() {
        LocalDateTime mockDate = LocalDateTime.of(2025, Month.DECEMBER, 14, 16, 25);
        LocalDateTime otherMockDate = LocalDateTime.of(2025, Month.DECEMBER, 12, 20, 30);

        MockedStatic<LocalDateTime> mocked = mockStatic(LocalDateTime.class);

        mocked.when(LocalDateTime::now).thenReturn(mockDate);
        SongCollection firstCollection = new SongCollection();

        mocked.when(LocalDateTime::now).thenReturn(otherMockDate);
        SongCollection secondCollection = new SongCollection();

        String result = "My collection is newer!";
        assertEquals(firstCollection.compareCollection(secondCollection), result);

        mocked.close();
    }
}