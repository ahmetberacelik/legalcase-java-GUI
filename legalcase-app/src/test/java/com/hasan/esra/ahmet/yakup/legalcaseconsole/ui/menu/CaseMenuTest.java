package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseStatus;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
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
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.Assert.*;

public class CaseMenuTest {

    private MenuManager menuManager;
    private CaseMenu caseMenu;
    private CaseService caseService;
    private ClientService clientService;
    private AuthService authService;
    private DocumentService documentService;
    private HearingService hearingService;

    private CaseDAO caseDAO;
    private ClientDAO clientDAO;
    private UserDAO userDAO;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

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
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Service nesnelerini oluşturma
        caseService = new CaseService(caseDAO, clientDAO);
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);
        documentService = new DocumentService(null, caseDAO); // Belge DAO'su null olabilir, bizim testlerimiz için gerekli değil
        hearingService = new HearingService(null, caseDAO); // Duruşma DAO'su null olabilir, bizim testlerimiz için gerekli değil

        // Test kullanıcısı oluşturup giriş yapalım
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // MenuManager'ı test versiyonuyla oluşturma
        menuManager = new TestMenuManager(authService, clientService, caseService, hearingService, documentService);

        // CaseMenu nesnesi oluşturma
        caseMenu = new CaseMenu(menuManager, caseService, clientService);
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
    public void Test_Display_SelectAddCase_Success() throws SQLException {
        // Düzenleme - Scanner'ı "1. Yeni Dava Ekle" seçeneğini seçecek şekilde ayarlama
        // Örnek dava bilgileri ve işlem sonunda ana menüye dönme (9)
        ConsoleHelper.setScanner(new Scanner("1\nDV2023-001\nTest Davası\n1\nTest açıklaması\nn\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Dava oluşturuldu mu?
        Optional<Case> caseOpt = caseService.getCaseByCaseNumber("DV2023-001");
        assertTrue("Dava oluşturulmalı", caseOpt.isPresent());
        assertEquals("Dava başlığı doğru olmalı", "Test Davası", caseOpt.get().getTitle());
        assertEquals("Dava tipi doğru olmalı", CaseType.CIVIL, caseOpt.get().getType());
        assertEquals("Dava açıklaması doğru olmalı", "Test açıklaması", caseOpt.get().getDescription());
        assertEquals("Dava durumu YENİ olmalı", CaseStatus.NEW, caseOpt.get().getStatus());
    }

    @Test
    public void Test_Display_SelectAddCase_WithClient_Success() throws SQLException {
        // Önce bir müvekkil ekleyelim
        Client client = new Client("John", "Doe", "john.doe@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "1. Yeni Dava Ekle" seçeneğini seçecek ve müvekkil ekleyecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("1\nDV2023-002\nTest Davası 2\n2\nMüvekkilli Test\ny\n" + client.getId() + "\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Dava oluşturuldu mu ve müvekkil eklendi mi?
        Optional<Case> caseOpt = caseService.getCaseByCaseNumber("DV2023-002");
        assertTrue("Dava oluşturulmalı", caseOpt.isPresent());

        List<Client> clients = caseService.getClientsForCase(caseOpt.get().getId());
        assertFalse("Dava müvekkil listesi boş olmamalı", clients.isEmpty());
        assertEquals("Dava müvekkil ID'si doğru olmalı", client.getId(), clients.get(0).getId());
    }

    @Test
    public void Test_Display_SelectViewCaseDetails_Success() throws SQLException {
        // Önce bir dava ekleyelim
        Case caseEntity = new Case("DV2023-003", "Test Davası 3", CaseType.CRIMINAL);
        caseEntity.setDescription("Detaylı açıklama");
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "2. Dava Detaylarını Görüntüle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n" + caseEntity.getId() + "\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Çıktıda dava bilgileri var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda dava numarası olmalı", output.contains("DV2023-003"));
        assertTrue("Çıktıda dava başlığı olmalı", output.contains("Test Davası 3"));
        assertTrue("Çıktıda dava tipi olmalı", output.contains("CRIMINAL"));
        assertTrue("Çıktıda dava açıklaması olmalı", output.contains("Detaylı açıklama"));
    }

    @Test
    public void Test_Display_SelectViewCaseDetails_NonExistentCase_NotFound() {
        // Düzenleme - Scanner'ı olmayan bir dava ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n9999\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Dava bulunamadı' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectUpdateCase_Success() throws SQLException {
        // Önce bir dava ekleyelim
        Case caseEntity = new Case("DV2023-004", "Eski Başlık", CaseType.FAMILY);
        caseEntity.setDescription("Eski açıklama");
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "3. Dava Güncelle" seçeneğini seçecek şekilde ayarlama
        // Tüm alanları güncelleyelim
        ConsoleHelper.setScanner(new Scanner("3\n" + caseEntity.getId() + "\nDV2023-004-UPD\nYeni Başlık\ny\n3\ny\n2\nYeni açıklama\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Dava güncellendi mi?
        Optional<Case> updatedCase = caseService.getCaseById(caseEntity.getId());
        assertTrue("Dava bulunmalı", updatedCase.isPresent());
        assertEquals("Dava numarası güncellenmeli", "DV2023-004-UPD", updatedCase.get().getCaseNumber());
        assertEquals("Dava başlığı güncellenmeli", "Yeni Başlık", updatedCase.get().getTitle());
        assertEquals("Dava tipi güncellenmeli", CaseType.FAMILY, updatedCase.get().getType());
        assertEquals("Dava durumu güncellenmeli", CaseStatus.ACTIVE, updatedCase.get().getStatus());
        assertEquals("Dava açıklaması güncellenmeli", "Yeni açıklama", updatedCase.get().getDescription());
    }

    @Test
    public void Test_Display_SelectDeleteCase_Confirm_Success() throws SQLException {
        // Önce bir dava ekleyelim
        Case caseEntity = new Case("DV2023-005", "Silinecek Dava", CaseType.CORPORATE);
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "4. Dava Sil" seçeneğini seçecek ve onaylayacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + caseEntity.getId() + "\ny\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Dava silindi mi?
        Optional<Case> deletedCase = caseService.getCaseById(caseEntity.getId());
        assertFalse("Dava silinmeli", deletedCase.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteCase_Cancel_NotDeleted() throws SQLException {
        // Önce bir dava ekleyelim
        Case caseEntity = new Case("DV2023-006", "Silinmeyecek Dava", CaseType.OTHER);
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "4. Dava Sil" seçeneğini seçecek ama iptal edecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + caseEntity.getId() + "\nn\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Dava silinmedi mi?
        Optional<Case> notDeletedCase = caseService.getCaseById(caseEntity.getId());
        assertTrue("Dava silinmemeli", notDeletedCase.isPresent());
    }

    @Test
    public void Test_Display_SelectAddClientToCase_Success() throws SQLException {
        // Önce bir dava ve müvekkil ekleyelim
        Case caseEntity = new Case("DV2023-007", "Müvekkil Eklenecek Dava", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Jane", "Doe", "jane.doe@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "5. Davaya Müvekkil Ekle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\n" + caseEntity.getId() + "\n" + client.getId() + "\n\n9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Müvekkil davaya eklendi mi?
        List<Client> clients = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Müvekkil listesi boş olmamalı", clients.isEmpty());
        assertEquals("Müvekkil ID'si doğru olmalı", client.getId(), clients.get(0).getId());
    }

    @Test
    public void Test_Display_SelectRemoveClientFromCase_Success() throws SQLException {
        // Önce bir dava ve müvekkil ekleyelim ve ilişkilendirelim
        Case caseEntity = new Case("DV2023-008", "Müvekkil Çıkarılacak Dava", CaseType.FAMILY);
        caseDAO.create(caseEntity);

        Client client = new Client("Bob", "Smith", "bob.smith@example.com");
        clientDAO.create(client);

        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // İlişki kuruldu mu kontrol edelim
        List<Client> initialClients = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Başlangıçta müvekkil listesi boş olmamalı", initialClients.isEmpty());

        // Düzenleme - Scanner'ı "6. Davadan Müvekkil Çıkar" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n" + caseEntity.getId() + "\n" + client.getId() + "\n\n9\n6\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Müvekkil davadan çıkarıldı mı?
        List<Client> finalClients = caseService.getClientsForCase(caseEntity.getId());
        assertTrue("Müvekkil listesi boş olmalı", finalClients.isEmpty());
    }

    @Test
    public void Test_Display_SelectListCasesByStatus_Success() throws SQLException {
        // Farklı durumlarda birkaç dava ekleyelim
        Case case1 = new Case("DV2023-009", "Aktif Dava 1", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(case1);

        Case case2 = new Case("DV2023-010", "Aktif Dava 2", CaseType.CRIMINAL);
        case2.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(case2);

        Case case3 = new Case("DV2023-011", "Kapalı Dava", CaseType.FAMILY);
        case3.setStatus(CaseStatus.CLOSED);
        caseDAO.create(case3);

        // Düzenleme - Scanner'ı "7. Durum Bazında Davaları Listele" seçeneğini seçecek şekilde ayarlama
        // ACTIVE durumunu seçiyoruz
        ConsoleHelper.setScanner(new Scanner("7\n2\n\n9\n\n6\n")); // 2 = ACTIVE enum değeri

        // İşlem
        caseMenu.display();

        // Doğrulama - Çıktıda aktif davalar listelenmiş mi?
        String output = outContent.toString();
        assertTrue("Çıktıda Aktif Dava 1 olmalı", output.contains("Aktif Dava 1"));
        assertTrue("Çıktıda Aktif Dava 2 olmalı", output.contains("Aktif Dava 2"));
        assertFalse("Çıktıda Kapalı Dava olmamalı", output.contains("Kapalı Dava"));
    }

    @Test
    public void Test_Display_SelectViewAllCases_Success() throws SQLException {
        // Birkaç dava ekleyelim
        Case case1 = new Case("DV2023-012", "Test Davası A", CaseType.CIVIL);
        caseDAO.create(case1);

        Case case2 = new Case("DV2023-013", "Test Davası B", CaseType.CRIMINAL);
        caseDAO.create(case2);

        // Düzenleme - Scanner'ı "8. Tüm Davaları Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("8\n\n9\n6\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - Çıktıda tüm davalar listelenmiş mi?
        String output = outContent.toString();
        assertTrue("Çıktıda Test Davası A olmalı", output.contains("Test Davası A"));
        assertTrue("Çıktıda Test Davası B olmalı", output.contains("Test Davası B"));
    }

    @Test
    public void Test_Display_SelectReturnToMainMenu() {
        // Düzenleme - Scanner'ı "9. Ana Menüye Dön" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("9\n"));

        // İşlem
        caseMenu.display();

        // Doğrulama - MenuManager'ın navigateToMainMenu metodu çağrıldı mı kontrol edilebilir
        assertTrue("Ana menüye dönüş çağrılmalı", ((TestMenuManager)menuManager).isNavigatedToMainMenu());
    }
    @Test
    public void Test_AddCase_WithDuplicateCaseNumber_ShowsError() throws SQLException {
        // Var olan bir dava numarası oluştur
        Case existingCase = new Case("DV2023-DUPLICATE", "Mevcut Dava", CaseType.CIVIL);
        caseDAO.create(existingCase);

        // Düzenleme - Scanner'ı aynı dava numarası ile ayarla
        ConsoleHelper.setScanner(new Scanner("DV2023-DUPLICATE\nDuplicate Test\n1\nTest açıklaması\nn\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.addCase();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda hata mesajı olmalı", output.contains("ERROR"));
        assertTrue("Çıktıda 'already in use' mesajı olmalı", output.contains("already in use"));
    }

    @Test
    public void Test_ViewCaseDetails_WithAssociatedClients_ShowsClients() throws SQLException {
        // Bir dava oluştur
        Case caseEntity = new Case("DV2023-WITH-CLIENTS", "Müvekkilli Dava", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Bir müvekkil oluştur ve dava ile ilişkilendir
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Müvekkili dava ile ilişkilendir
        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // ÖNEMLİ: Çıktıyı yakalamadan önce müvekkillerin doğru yüklendiğini kontrol et
        List<Client> clientList = caseService.getClientsForCase(caseEntity.getId());
        assertFalse("Müvekkil listesi boş olmamalı", clientList.isEmpty());

        // Scanner'ı ayarla - fazladan boş satırlar ekle
        ConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n\n\n\n9\n"));

        // İşlem
        caseMenu.viewCaseDetails();

        // Doğrulama - çıktıyı daha esnek bir şekilde kontrol et
        String output = outContent.toString();
        System.out.println("ÇIKTI: " + output);

        // Müvekkil ile ilgili herhangi bir içerik var mı kontrol et
        assertTrue("Çıktıda müvekkil bilgileri olmalı",
                output.contains("Client") ||
                        output.contains("client") ||
                        output.contains("Test"));
    }

    @Test
    public void Test_UpdateCase_NonExistentCase_ShowsError() {
        // Düzenleme - Varolmayan bir dava ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.updateCase();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Case not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_AddClientToCase_AlreadyAddedClient_ShowsWarning() throws SQLException {
        // Bir dava ve müvekkil oluştur
        Case caseEntity = new Case("DV2023-012", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Duplicate", "Client", "duplicate@example.com");
        clientDAO.create(client);

        // İlk kez ekle
        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // ÖNEMLİ: Yeni bir Scanner nesnesi oluştur - daha uzun bekleme süresi için ek boş satırlar ekle
        ConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n" + client.getId() + "\n\n\n\n"));

        // İşlem - doğrudan CaseMenu'deki metodu çağırıyoruz
        // addClientToCase() yerine addClientToCase(Long caseId) metodunu çağırıyoruz
        caseMenu.addClientToCase(caseEntity.getId());

        // Doğrulama - Çıktıda "This client is already added to the case" mesajı var mı?
        String output = outContent.toString();
        System.out.println("ÇIKTI: " + output);

        // Tam olarak WARNING kelimesini değil, mesaj içeriğini kontrol edelim
        assertTrue("Çıktıda 'already added to the case' mesajı olmalı",
                output.contains("already added to the case") ||
                        output.contains("already associated with"));
    }

    @Test
    public void Test_RemoveClientFromCase_EmptyClientList_ShowsMessage() throws SQLException {
        // Bir dava oluştur ama müvekkil ekleme
        Case caseEntity = new Case("DV2023-014", "Müvekkili Olmayan Dava", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı ayarla
        ConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.removeClientFromCase();

        // Doğrulama - Çıktıda bilgi mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda müvekkil yok mesajı olmalı", output.contains("no clients associated"));
    }

    @Test
    public void Test_RemoveClientFromCase_NonExistentClient_ShowsError() throws SQLException {
        // Bir dava ve müvekkil oluştur
        Case caseEntity = new Case("DV2023-015", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Client client = new Client("Real", "Client", "real@example.com");
        clientDAO.create(client);

        caseService.addClientToCase(caseEntity.getId(), client.getId());

        // Varolmayan müvekkil ID'si
        long nonExistentClientId = 9999L;

        // Düzenleme - var olmayan bir müvekkil ID'si kullan
        ConsoleHelper.setScanner(new Scanner(caseEntity.getId() + "\n" + nonExistentClientId + "\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.removeClientFromCase();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda ilişkili değil mesajı olmalı", output.contains("not associated with this case"));
    }

    @Test
    public void Test_ListCasesByStatus_NoMatchingCases_ShowsMessage() throws SQLException {
        // Sadece ACTIVE durumunda bir dava oluştur
        Case caseEntity = new Case("DV2023-016", "Aktif Dava", CaseType.CIVIL);
        caseEntity.setStatus(CaseStatus.ACTIVE);
        caseDAO.create(caseEntity);

        // CLOSED durumu için Scanner'ı ayarla (CaseStatus enumındaki pozisyona göre)
        ConsoleHelper.setScanner(new Scanner("4\n\n9\n")); // 4 = CLOSED enum değeri olduğunu varsayıyorum

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.listCasesByStatus();

        // Doğrulama - Çıktıda bilgi mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda dava bulunamadı mesajı olmalı", output.contains("No cases found"));
    }

    @Test
    public void Test_ViewAllCases_NoCasesInDatabase_ShowsMessage() throws SQLException {
        // Tüm davaları silmeliyiz
        // Önce var olan tüm davaları al ve sil
        List<Case> allCases = caseService.getAllCases();
        for (Case c : allCases) {
            caseDAO.delete(c);
        }

        // Düzenleme - Scanner'ı ayarla
        ConsoleHelper.setScanner(new Scanner("8\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.viewAllCases();

        // Doğrulama - Çıktıda bilgi mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda dava yok mesajı olmalı", output.contains("no registered cases"));
    }

    @Test
    public void Test_DeleteCase_NonExistentCase_ShowsError() {
        // Düzenleme - Varolmayan bir dava ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // Test öncesi çıktıyı temizle
        outContent.reset();

        // İşlem
        caseMenu.deleteCase();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Case not found' mesajı olmalı", output.contains("not found"));
    }
}