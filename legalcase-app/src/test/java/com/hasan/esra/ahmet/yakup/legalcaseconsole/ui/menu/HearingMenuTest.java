package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.HearingDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Hearing;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.HearingStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.AuthService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.CaseService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.ClientService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.DocumentService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.HearingService;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.ConsoleHelper;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.MenuManager;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.util.TestDatabaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.*;

public class HearingMenuTest {

    private MenuManager menuManager;
    private HearingMenu hearingMenu;
    private HearingService hearingService;
    private CaseService caseService;
    private AuthService authService;
    private DocumentService documentService;
    private ClientService clientService;

    private HearingDAO hearingDAO;
    private CaseDAO caseDAO;
    private UserDAO userDAO;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Test için kullanılacak dava
    private Case testCase;

    // MenuManager'ı test için genişleten iç sınıf
    private class TestMenuManager extends MenuManager {
        private boolean navigatedToMainMenu = false;

        public TestMenuManager(AuthService authService, ClientService clientService,
                               CaseService caseService, HearingService hearingService,
                               DocumentService documentService) {
            super(authService, clientService, caseService, hearingService, documentService);
        }

        @Override
        public void navigateToMainMenu() {
            navigatedToMainMenu = true;
            // Burada test için hiçbir şey yapmadan başarılı işaretlenecek
        }

        public boolean isNavigatedToMainMenu() {
            return navigatedToMainMenu;
        }
    }

    @Before
    public void setUp() throws SQLException {
        // Konsol çıktısını yakalama
        System.setOut(new PrintStream(outContent));

        // Test veritabanını kurma
        TestDatabaseManager.createTables();

        // DAO nesnelerini oluşturma
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        hearingDAO = new HearingDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Service nesnelerini oluşturma
        caseService = new CaseService(caseDAO, null); // ClientDAO null olabilir, bizim testlerimiz için gerekli değil
        clientService = new ClientService(null);
        authService = new AuthService(userDAO);
        documentService = new DocumentService(null, caseDAO);
        hearingService = new HearingService(hearingDAO, caseDAO);

        // Test kullanıcısı oluşturup giriş yapalım
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // Test davası oluşturma
        testCase = new Case("DV2023-TEST", "Test Davası", CaseType.CIVIL);
        testCase.setDescription("Test davası açıklaması");
        testCase.setStatus(CaseStatus.NEW);
        caseDAO.create(testCase);

        // MenuManager'ı test versiyonuyla oluşturma
        menuManager = new TestMenuManager(authService, clientService, caseService, hearingService, documentService);

        // HearingMenu nesnesi oluşturma
        hearingMenu = new HearingMenu(menuManager, hearingService, caseService);
    }

    @After
    public void tearDown() throws SQLException {
        // Konsol çıktısını eski haline getirme
        System.setOut(originalOut);

        // Scanner'ı sıfırlama
        ConsoleHelper.resetScanner();

        // Test veritabanı bağlantısını kapatma
        TestDatabaseManager.closeConnection();
    }

    @Test
    public void Test_Display_SelectAddHearing_Success() throws SQLException {
        // Düzenleme - Scanner'ı "1. Yeni Duruşma Ekle" seçeneğini seçecek şekilde ayarlama
        // Örnek duruşma bilgileri ve işlem sonunda ana menüye dönme (10)
        LocalDate hearingDate = LocalDate.now().plusDays(7);
        LocalTime hearingTime = LocalTime.of(10, 0);

        ConsoleHelper.setScanner(new Scanner("1\n" + testCase.getId() + "\n" + hearingDate.toString() + "\n" +
                hearingTime.toString() + "\nHakim Adı\nAdliye Binası\nTest notları\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma oluşturuldu mu?
        List<Hearing> hearings = hearingService.getHearingsByCaseId(testCase.getId());
        assertFalse("Duruşma listesi boş olmamalı", hearings.isEmpty());
        assertEquals("Duruşma hakimi doğru olmalı", "Hakim Adı", hearings.get(0).getJudge());
        assertEquals("Duruşma lokasyonu doğru olmalı", "Adliye Binası", hearings.get(0).getLocation());
        assertEquals("Duruşma notları doğru olmalı", "Test notları", hearings.get(0).getNotes());
        assertEquals("Duruşma durumu SCHEDULED olmalı", HearingStatus.SCHEDULED, hearings.get(0).getStatus());
    }

    @Test
    public void Test_Display_SelectAddHearing_CaseNotFound_Error() {
        // Düzenleme - Scanner'ı olmayan bir dava ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("1\n9999\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Dava bulunamadı' mesajı olmalı", output.contains("Case with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectViewHearingDetails_Success() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Hakim", "Test Lokasyon", "Test Notları");

        // Düzenleme - Scanner'ı "2. Duruşma Detaylarını Görüntüle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n" + hearing.getId() + "\n\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda duruşma bilgileri var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda duruşma ID'si olmalı", output.contains("Hearing ID: " + hearing.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingDetails_NonExistentHearing_NotFound() {
        // Düzenleme - Scanner'ı olmayan bir duruşma ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n9999\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Duruşma bulunamadı' mesajı olmalı", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectUpdateHearing_Success() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Eski Hakim", "Eski Lokasyon", "Eski Notlar");

        // Düzenleme - Scanner'ı "3. Duruşma Güncelle" seçeneğini seçecek şekilde ayarlama
        // Tüm alanları güncelleyelim
        ConsoleHelper.setScanner(new Scanner("3\n" + hearing.getId() + "\ny\nYeni Hakim\ny\nYeni Lokasyon\ny\nYeni Notlar\ny\n1\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma güncellendi mi?
        Optional<Hearing> updatedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Duruşma bulunmalı", updatedHearingOpt.isPresent());
        Hearing updatedHearing = updatedHearingOpt.get();
        assertEquals("Duruşma hakimi güncellenmiş olmalı", "Yeni Hakim", updatedHearing.getJudge());
        assertEquals("Duruşma lokasyonu güncellenmiş olmalı", "Yeni Lokasyon", updatedHearing.getLocation());
        assertEquals("Duruşma notları güncellenmiş olmalı", "Yeni Notlar", updatedHearing.getNotes());
        assertEquals("Duruşma durumu güncellenmiş olmalı", HearingStatus.SCHEDULED, updatedHearing.getStatus());
    }

    @Test
    public void Test_Display_SelectUpdateHearing_NonExistentHearing_NotFound() {
        // Düzenleme - Scanner'ı olmayan bir duruşma ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("3\n9999\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Duruşma bulunamadı' mesajı olmalı", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectRescheduleHearing_Success() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Hakim", "Test Lokasyon", "Test Notları");

        // Düzenleme - Scanner'ı "4. Duruşma Yeniden Planla" seçeneğini seçecek şekilde ayarlama
        LocalDate newDate = LocalDate.now().plusDays(14);
        LocalTime newTime = LocalTime.of(14, 30);
        ConsoleHelper.setScanner(new Scanner("4\n" + hearing.getId() + "\n" + newDate.toString() + "\n" + newTime.toString() + "\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma yeniden planlandı mı?
        Optional<Hearing> rescheduledHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Duruşma bulunmalı", rescheduledHearingOpt.isPresent());
        Hearing rescheduledHearing = rescheduledHearingOpt.get();

        // Tarih ve saat kontrolü - Yıl, ay, gün, saat ve dakika kontrolü
        LocalDateTime expectedDateTime = LocalDateTime.of(newDate, newTime);
        assertEquals("Duruşma tarihi güncellenmiş olmalı - Yıl", expectedDateTime.getYear(), rescheduledHearing.getHearingDate().getYear());
        assertEquals("Duruşma tarihi güncellenmiş olmalı - Ay", expectedDateTime.getMonth(), rescheduledHearing.getHearingDate().getMonth());
        assertEquals("Duruşma tarihi güncellenmiş olmalı - Gün", expectedDateTime.getDayOfMonth(), rescheduledHearing.getHearingDate().getDayOfMonth());
        assertEquals("Duruşma saati güncellenmiş olmalı - Saat", expectedDateTime.getHour(), rescheduledHearing.getHearingDate().getHour());
        assertEquals("Duruşma saati güncellenmiş olmalı - Dakika", expectedDateTime.getMinute(), rescheduledHearing.getHearingDate().getMinute());

        // Duruşma durumu kontrolü
        assertEquals("Duruşma durumu SCHEDULED olmalı", HearingStatus.SCHEDULED, rescheduledHearing.getStatus());

        // Notlar kontrolü - Yeniden planlama notu eklenmiş olmalı
        assertTrue("Duruşma notlarında yeniden planlama bilgisi olmalı", rescheduledHearing.getNotes().contains("Hearing rescheduled from"));
    }

    @Test
    public void Test_Display_SelectRescheduleHearing_NonExistentHearing_NotFound() {
        // Düzenleme - Scanner'ı olmayan bir duruşma ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n9999\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Duruşma bulunamadı' mesajı olmalı", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectUpdateHearingStatus_Success() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Hakim", "Test Lokasyon", "Test Notları");

        // Düzenleme - Scanner'ı "5. Duruşma Durumunu Değiştir" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\n" + hearing.getId() + "\n2\n\n10\n")); // 2 = COMPLETED durumu

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma durumu güncellendi mi?
        Optional<Hearing> updatedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Duruşma bulunmalı", updatedHearingOpt.isPresent());
        assertEquals("Duruşma durumu COMPLETED olmalı", HearingStatus.COMPLETED, updatedHearingOpt.get().getStatus());
    }

    @Test
    public void Test_Display_SelectUpdateHearingStatus_NonExistentHearing_NotFound() {
        // Düzenleme - Scanner'ı olmayan bir duruşma ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("5\n9999\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Duruşma bulunamadı' mesajı olmalı", output.contains("Hearing with specified ID not found"));
    }

    @Test
    public void Test_Display_SelectDeleteHearing_Success() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Hakim", "Test Lokasyon", "Test Notları");

        // Düzenleme - Scanner'ı "6. Duruşma Sil" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n" + hearing.getId() + "\ny\n\n10\n")); // Silme işlemini onaylama

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma silindi mi?
        Optional<Hearing> deletedHearingOpt = hearingService.getHearingById(hearing.getId());
        assertFalse("Duruşma silinmiş olmalı", deletedHearingOpt.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteHearing_Cancelled_NotDeleted() throws SQLException {
        // Önce bir duruşma ekleyelim
        LocalDateTime hearingDate = LocalDateTime.now().plusDays(7);
        Hearing hearing = hearingService.createHearing(testCase.getId(), hearingDate, "Test Hakim", "Test Lokasyon", "Test Notları");

        // Düzenleme - Scanner'ı "6. Duruşma Sil" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n" + hearing.getId() + "\nn\n\n10\n")); // Silme işlemini iptal etme

        // İşlem
        hearingMenu.display();

        // Doğrulama - Duruşma silinmedi mi?
        Optional<Hearing> hearingOpt = hearingService.getHearingById(hearing.getId());
        assertTrue("Duruşma silinmemiş olmalı", hearingOpt.isPresent());

        // Çıktıda iptal mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'İşlem iptal edildi' mesajı olmalı", output.contains("Operation cancelled"));
    }

    @Test
    public void Test_Display_SelectViewUpcomingHearings_Success() throws SQLException {
        // Birkaç duruşma ekleyelim
        LocalDateTime pastDate = LocalDateTime.now().minusDays(7);
        LocalDateTime futureDate1 = LocalDateTime.now().plusDays(3);
        LocalDateTime futureDate2 = LocalDateTime.now().plusDays(7);

        // Geçmiş duruşma
        hearingService.createHearing(testCase.getId(), pastDate, "Geçmiş Hakim", "Geçmiş Lokasyon", "Geçmiş Notlar");

        // Gelecek duruşmalar
        Hearing future1 = hearingService.createHearing(testCase.getId(), futureDate1, "Gelecek Hakim 1", "Gelecek Lokasyon 1", "Gelecek Notlar 1");
        Hearing future2 = hearingService.createHearing(testCase.getId(), futureDate2, "Gelecek Hakim 2", "Gelecek Lokasyon 2", "Gelecek Notlar 2");

        // Düzenleme - Scanner'ı "7. Yaklaşan Duruşmaları Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("7\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda gelecek duruşmalar var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda gelecek duruşma 1 ID'si olmalı", output.contains("ID: " + future1.getId()));
        assertTrue("Çıktıda gelecek duruşma 2 ID'si olmalı", output.contains("ID: " + future2.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingsByCase_Success() throws SQLException {
        // Birkaç duruşma ekleyelim
        LocalDateTime date1 = LocalDateTime.now().plusDays(3);
        LocalDateTime date2 = LocalDateTime.now().plusDays(7);

        Hearing hearing1 = hearingService.createHearing(testCase.getId(), date1, "Hakim 1", "Lokasyon 1", "Notlar 1");
        Hearing hearing2 = hearingService.createHearing(testCase.getId(), date2, "Hakim 2", "Lokasyon 2", "Notlar 2");

        // Düzenleme - Scanner'ı "8. Davaya Göre Duruşmaları Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("8\n" + testCase.getId() + "\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda duruşmalar var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda duruşma 1 ID'si olmalı", output.contains("ID: " + hearing1.getId()));
        assertTrue("Çıktıda duruşma 2 ID'si olmalı", output.contains("ID: " + hearing2.getId()));
    }

    @Test
    public void Test_Display_SelectViewHearingsByDateRange_Success() throws SQLException {
        // Birkaç duruşma ekleyelim
        LocalDateTime date1 = LocalDateTime.now().plusDays(3); // Tarih aralığı içinde
        LocalDateTime date2 = LocalDateTime.now().plusDays(7); // Tarih aralığı içinde
        LocalDateTime date3 = LocalDateTime.now().plusDays(15); // Tarih aralığı dışında

        Hearing hearing1 = hearingService.createHearing(testCase.getId(), date1, "Hakim 1", "Lokasyon 1", "Notlar 1");
        Hearing hearing2 = hearingService.createHearing(testCase.getId(), date2, "Hakim 2", "Lokasyon 2", "Notlar 2");
        Hearing hearing3 = hearingService.createHearing(testCase.getId(), date3, "Hakim 3", "Lokasyon 3", "Notlar 3");

        // Düzenleme - Scanner'ı "9. Tarih Aralığına Göre Duruşmaları Listele" seçeneğini seçecek şekilde ayarlama
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        ConsoleHelper.setScanner(new Scanner("9\n" + startDate.toString() + "\n" + endDate.toString() + "\n\n10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Çıktıda tarih aralığındaki duruşmalar var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda duruşma 1 ID'si olmalı", output.contains("ID: " + hearing1.getId()));
        assertTrue("Çıktıda duruşma 2 ID'si olmalı", output.contains("ID: " + hearing2.getId()));
        assertFalse("Çıktıda duruşma 3 ID'si olmamalı", output.contains("ID: " + hearing3.getId()));
    }

    @Test
    public void Test_Display_SelectReturnToMainMenu_Success() {
        // Düzenleme - Scanner'ı "10. Ana Menüye Dön" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("10\n"));

        // İşlem
        hearingMenu.display();

        // Doğrulama - Ana menüye yönlendirildi mi?
        TestMenuManager testMenuManager = (TestMenuManager) menuManager;
        assertTrue("Ana menüye yönlendirilmiş olmalı", testMenuManager.isNavigatedToMainMenu());
    }
}
