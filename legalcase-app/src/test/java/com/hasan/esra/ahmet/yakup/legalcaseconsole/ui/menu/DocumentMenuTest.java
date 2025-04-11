package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.CaseDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.DocumentDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Case;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Document;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.CaseType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.DocumentType;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*;
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

public class DocumentMenuTest {

    private MenuManager menuManager;
    private DocumentMenu documentMenu;
    private DocumentService documentService;
    private CaseService caseService;
    private AuthService authService;

    private DocumentDAO documentDAO;
    private CaseDAO caseDAO;
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
            // Test için hiçbir şey yapmadan başarılı işaretlenecek
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
        documentDAO = new DocumentDAO(TestDatabaseManager.getConnectionSource());
        caseDAO = new CaseDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Service nesnelerini oluşturma
        documentService = new DocumentService(documentDAO, caseDAO);
        caseService = new CaseService(caseDAO, null); // ClientDAO null olabilir, testlerimiz için gerekli değil
        authService = new AuthService(userDAO);

        // Test kullanıcısı oluşturup giriş yapalım
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // MenuManager'ı test versiyonuyla oluşturma
        menuManager = new TestMenuManager(authService, null, caseService, null, documentService);

        // DocumentMenu nesnesi oluşturma
        documentMenu = new DocumentMenu(menuManager, documentService, caseService);
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
    public void Test_Display_SelectReturnToMainMenu() {
        // Düzenleme - Scanner'ı "9. Ana Menüye Dön" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - MenuManager'ın navigateToMainMenu metodu çağrıldı mı kontrol edilebilir
        assertTrue("Ana menüye dönüş çağrılmalı", ((TestMenuManager)menuManager).isNavigatedToMainMenu());
    }

    @Test
    public void Test_Display_SelectAddDocument_Success() throws SQLException {
        // Önce bir dava oluşturalım
        Case caseEntity = new Case("DOC2023-001", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "1. Yeni Belge Ekle" seçeneğini seçecek şekilde ayarlama
        // Örnek belge bilgileri ve işlem sonunda ana menüye dönme (9)
        ConsoleHelper.setScanner(new Scanner("1\n" + caseEntity.getId() + "\nTest Belge\n1\nBu bir test belgesidir.\n\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Belge oluşturuldu mu?
        List<Document> documents = documentService.getDocumentsByCaseId(caseEntity.getId());
        assertFalse("Belge listesi boş olmamalı", documents.isEmpty());
        assertEquals("Belge başlığı doğru olmalı", "Test Belge", documents.get(0).getTitle());
        assertEquals("Belge tipi doğru olmalı", DocumentType.CONTRACT, documents.get(0).getType());
        assertEquals("Belge içeriği doğru olmalı", "Bu bir test belgesidir.", documents.get(0).getContent());
    }

    @Test
    public void Test_Display_SelectAddDocument_NoCaseExists() {
        // Hiç dava oluşturmuyoruz

        // Düzenleme - Scanner'ı "1. Yeni Belge Ekle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("1\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Hata mesajı görüntülendi mi?
        String output = outContent.toString();
        assertTrue("Dava oluşturma hatası mesajı gösterilmeli",
                output.contains("You must create a case first") ||
                        output.contains("create a case first"));
    }

    @Test
    public void Test_Display_SelectViewDocumentDetails_Success() throws SQLException {
        // Önce bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-002", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Test Belge Detayları", DocumentType.EVIDENCE, caseEntity, "Bu bir test belgesidir.");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "2. Belge Detaylarını Görüntüle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n" + document.getId() + "\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda belge bilgileri var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda belge başlığı olmalı", output.contains("Test Belge Detayları"));
        assertTrue("Çıktıda belge tipi olmalı", output.contains("EVIDENCE"));
        assertTrue("Çıktıda belge içeriği olmalı", output.contains("Bu bir test belgesidir."));
    }

    @Test
    public void Test_Display_SelectViewDocumentDetails_NonExistentDocument() {
        // Düzenleme - Scanner'ı olmayan bir belge ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n9999\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Belge bulunamadı' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectUpdateDocument_Success() throws SQLException {
        // Önce bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-003", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Eski Belge Başlığı", DocumentType.CONTRACT, caseEntity, "Eski içerik");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "3. Belge Güncelle" seçeneğini seçecek şekilde ayarlama
        // Başlık ve içeriği güncelleyelim, tipi aynı kalsın
        ConsoleHelper.setScanner(new Scanner("3\n" + document.getId() + "\nYeni Belge Başlığı\nn\ny\nYeni içerik.\n\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Belge güncellendi mi?
        Optional<Document> updatedDoc = documentService.getDocumentById(document.getId());
        assertTrue("Belge bulunmalı", updatedDoc.isPresent());
        assertEquals("Belge başlığı güncellenmeli", "Yeni Belge Başlığı", updatedDoc.get().getTitle());
        assertEquals("Belge tipi aynı kalmalı", DocumentType.CONTRACT, updatedDoc.get().getType());
        assertEquals("Belge içeriği güncellenmeli", "Yeni içerik.", updatedDoc.get().getContent());
    }

    @Test
    public void Test_Display_SelectUpdateDocument_UpdateType() throws SQLException {
        // Önce bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-004", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Belge Tipi Değişecek", DocumentType.CONTRACT, caseEntity, "Test içeriği");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "3. Belge Güncelle" seçeneğini seçecek şekilde ayarlama
        // Sadece belge tipini değiştirelim
        ConsoleHelper.setScanner(new Scanner("3\n" + document.getId() + "\n\ny\n2\nn\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Belge güncellendi mi?
        Optional<Document> updatedDoc = documentService.getDocumentById(document.getId());
        assertTrue("Belge bulunmalı", updatedDoc.isPresent());
        assertEquals("Belge başlığı aynı kalmalı", "Belge Tipi Değişecek", updatedDoc.get().getTitle());
        assertEquals("Belge tipi güncellenmeli", DocumentType.EVIDENCE, updatedDoc.get().getType()); // EVIDENCE enum değeri 2 konumunda olduğunu varsayarak
        assertEquals("Belge içeriği aynı kalmalı", "Test içeriği", updatedDoc.get().getContent());
    }

    @Test
    public void Test_Display_SelectDeleteDocument_Confirm_Success() throws SQLException {
        // Önce bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-005", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Silinecek Belge", DocumentType.CONTRACT, caseEntity, "Silinecek içerik");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "4. Belge Sil" seçeneğini seçecek ve onaylayacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + document.getId() + "\ny\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Belge silindi mi?
        Optional<Document> deletedDoc = documentService.getDocumentById(document.getId());
        assertFalse("Belge silinmeli", deletedDoc.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteDocument_Cancel_NotDeleted() throws SQLException {
        // Önce bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-006", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Silinmeyecek Belge", DocumentType.CONTRACT, caseEntity, "Korunacak içerik");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "4. Belge Sil" seçeneğini seçecek ama iptal edecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + document.getId() + "\nn\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Belge silinmedi mi?
        Optional<Document> notDeletedDoc = documentService.getDocumentById(document.getId());
        assertTrue("Belge silinmemeli", notDeletedDoc.isPresent());
    }

    @Test
    public void Test_Display_SelectSearchDocumentsByTitle_Success() throws SQLException {
        // Birkaç dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-007", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document doc1 = new Document("Arama Test Belgesi", DocumentType.CONTRACT, caseEntity, "İçerik 1");
        documentDAO.create(doc1);

        Document doc2 = new Document("Başka Belge", DocumentType.EVIDENCE, caseEntity, "İçerik 2");
        documentDAO.create(doc2);

        // Düzenleme - Scanner'ı "5. Başlığa Göre Belge Ara" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\nArama\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda arama sonucu var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Arama Test Belgesi' olmalı", output.contains("Arama Test Belgesi"));
        assertFalse("Çıktıda 'Başka Belge' olmamalı", output.contains("Başka Belge"));
    }

    @Test
    public void Test_Display_SelectSearchDocumentsByTitle_NoResults() throws SQLException {
        // Bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-008", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Örnek Belge", DocumentType.CONTRACT, caseEntity, "İçerik");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "5. Başlığa Göre Belge Ara" seçeneğini seçecek ve eşleşmeyen bir terim kullanacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\nBulunamayacak\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda sonuç bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'No documents found' mesajı olmalı", output.contains("No documents found"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByType_Success() throws SQLException {
        // Birkaç dava ve farklı tiplerde belgeler oluşturalım
        Case caseEntity = new Case("DOC2023-009", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document doc1 = new Document("Contract 1", DocumentType.CONTRACT, caseEntity, "Contract içeriği 1");
        documentDAO.create(doc1);

        Document doc2 = new Document("Contract 2", DocumentType.CONTRACT, caseEntity, "Contract içeriği 2");
        documentDAO.create(doc2);

        Document doc3 = new Document("Evidence 1", DocumentType.EVIDENCE, caseEntity, "Evidence içeriği");
        documentDAO.create(doc3);

        // Düzenleme - Scanner'ı "6. Tipe Göre Belgeleri Listele" seçeneğini ve CONTRACT tipini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n1\n\n9\n")); // 1 = CONTRACT enum değeri olduğunu varsayarak

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda CONTRACT tipleri var mı ve EVIDENCE tipi yok mu?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Contract 1' olmalı", output.contains("Contract 1"));
        assertTrue("Çıktıda 'Contract 2' olmalı", output.contains("Contract 2"));
        assertFalse("Çıktıda 'Evidence 1' olmamalı", output.contains("Evidence 1"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_Success() throws SQLException {
        // İki farklı dava ve her davaya ait belgeler oluşturalım
        Case case1 = new Case("DOC2023-010", "Dava 1", CaseType.CIVIL);
        caseDAO.create(case1);

        Case case2 = new Case("DOC2023-011", "Dava 2", CaseType.CRIMINAL);
        caseDAO.create(case2);

        Document doc1 = new Document("Dava 1 Belgesi", DocumentType.CONTRACT, case1, "İçerik 1");
        documentDAO.create(doc1);

        Document doc2 = new Document("Dava 2 Belgesi", DocumentType.EVIDENCE, case2, "İçerik 2");
        documentDAO.create(doc2);

        // Düzenleme - Scanner'ı "7. Davaya Göre Belgeleri Listele" seçeneğini ve case1'i seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("7\n" + case1.getId() + "\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda case1'e ait belge var mı ve case2'ye ait belge yok mu?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Dava 1 Belgesi' olmalı", output.contains("Dava 1 Belgesi"));
        assertFalse("Çıktıda 'Dava 2 Belgesi' olmamalı", output.contains("Dava 2 Belgesi"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_NoCases() {
        // Hiç dava oluşturmuyoruz

        // Düzenleme - Scanner'ı "7. Davaya Göre Belgeleri Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("7\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda dava bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'There are no registered cases' mesajı olmalı",
                output.contains("no registered cases") ||
                        output.contains("No registered cases"));
    }

    @Test
    public void Test_Display_SelectListDocumentsByCase_NoDocuments() throws SQLException {
        // Bir dava oluşturalım ama belge eklememeelim
        Case caseEntity = new Case("DOC2023-012", "Belgesi Olmayan Dava", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        // Düzenleme - Scanner'ı "7. Davaya Göre Belgeleri Listele" seçeneğini ve caseEntity'yi seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("7\n" + caseEntity.getId() + "\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda belge bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'There are no documents for this case' mesajı olmalı",
                output.contains("no documents for this case") ||
                        output.contains("No documents for this case"));
    }

    @Test
    public void Test_Display_SelectViewAllDocuments_Success() throws SQLException {
        // Bir dava ve belge oluşturalım
        Case caseEntity = new Case("DOC2023-013", "Test Davası", CaseType.CIVIL);
        caseDAO.create(caseEntity);

        Document document = new Document("Tüm Belgeler Testi", DocumentType.CONTRACT, caseEntity, "İçerik");
        documentDAO.create(document);

        // Düzenleme - Scanner'ı "8. Tüm Belgeleri Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("8\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda belge bilgileri var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Tüm Belgeler Testi' olmalı", output.contains("Tüm Belgeler Testi"));
    }

    @Test
    public void Test_Display_SelectViewAllDocuments_NoDocuments() {
        // Hiç belge oluşturmuyoruz

        // Düzenleme - Scanner'ı "8. Tüm Belgeleri Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("8\n\n9\n"));

        // İşlem
        documentMenu.display();

        // Doğrulama - Çıktıda belge bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'There are no registered documents' mesajı olmalı",
                output.contains("no registered documents") ||
                        output.contains("No registered documents"));
    }

    @Test
    public void Test_ViewDocumentDetails_NonExistentDocument() {
        // Düzenleme - Varolmayan bir belge ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // İşlem
        documentMenu.viewDocumentDetails();

        // Doğrulama - Çıktıda belge bulunamadı hatası var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Document with specified ID not found' mesajı olmalı",
                output.contains("not found"));
    }

    @Test
    public void Test_UpdateDocument_NonExistentDocument() {
        // Düzenleme - Varolmayan bir belge ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // İşlem
        documentMenu.updateDocument();

        // Doğrulama - Çıktıda belge bulunamadı hatası var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Document with specified ID not found' mesajı olmalı",
                output.contains("not found"));
    }

    @Test
    public void Test_DeleteDocument_NonExistentDocument() {
        // Düzenleme - Varolmayan bir belge ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n9\n"));

        // İşlem
        documentMenu.deleteDocument();

        // Doğrulama - Çıktıda belge bulunamadı hatası var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Document with specified ID not found' mesajı olmalı",
                output.contains("not found"));
    }
}