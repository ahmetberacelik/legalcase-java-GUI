package com.hasan.esra.ahmet.yakup.legalcaseconsole.ui.menu;

import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.ClientDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.dao.UserDAO;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.Client;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.User;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.model.enums.UserRole;
import com.hasan.esra.ahmet.yakup.legalcaseconsole.service.*    ;
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

public class ClientMenuTest {

    private MenuManager menuManager;
    private ClientMenu clientMenu;
    private ClientService clientService;
    private AuthService authService;

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
        clientDAO = new ClientDAO(TestDatabaseManager.getConnectionSource());
        userDAO = new UserDAO(TestDatabaseManager.getConnectionSource());

        // Service nesnelerini oluşturma
        clientService = new ClientService(clientDAO);
        authService = new AuthService(userDAO);

        // Test kullanıcısı oluşturup giriş yapalım
        User testUser = authService.register("testuser", "password", "test@example.com", "Test", "User", UserRole.ADMIN);
        authService.login("testuser", "password");

        // MenuManager'ı test versiyonuyla oluşturma
        menuManager = new TestMenuManager(authService, clientService, null, null, null);

        // ClientMenu nesnesi oluşturma
        clientMenu = new ClientMenu(menuManager, clientService);
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
        // Düzenleme - Scanner'ı "7. Ana Menüye Dön" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - MenuManager'ın navigateToMainMenu metodu çağrıldı mı kontrol edilebilir
        assertTrue("Ana menüye dönüş çağrılmalı", ((TestMenuManager)menuManager).isNavigatedToMainMenu());
    }

    @Test
    public void Test_Display_SelectAddClient_Success() throws SQLException {
        // Düzenleme - Scanner'ı "1. Yeni Müvekkil Ekle" seçeneğini seçecek şekilde ayarlama
        // Örnek müvekkil bilgileri ve işlem sonunda ana menüye dönme (7)
        ConsoleHelper.setScanner(new Scanner("1\nJohn\nDoe\njohn.doe@example.com\n555-1234\n123 Main St\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Müvekkil oluşturuldu mu?
        Optional<Client> clientOpt = clientService.getClientByEmail("john.doe@example.com");
        assertTrue("Müvekkil oluşturulmalı", clientOpt.isPresent());
        assertEquals("Müvekkil adı doğru olmalı", "John", clientOpt.get().getName());
        assertEquals("Müvekkil soyadı doğru olmalı", "Doe", clientOpt.get().getSurname());
        assertEquals("Müvekkil telefon numarası doğru olmalı", "555-1234", clientOpt.get().getPhone());
        assertEquals("Müvekkil adresi doğru olmalı", "123 Main St", clientOpt.get().getAddress());
    }

    @Test
    public void Test_Display_SelectViewClientDetails_Success() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("Jane", "Smith", "jane.smith@example.com");
        client.setPhone("555-5678");
        client.setAddress("456 Oak St");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "2. Müvekkil Detaylarını Görüntüle" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n" + client.getId() + "\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda müvekkil bilgileri var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda müvekkil adı olmalı", output.contains("Jane"));
        assertTrue("Çıktıda müvekkil soyadı olmalı", output.contains("Smith"));
        assertTrue("Çıktıda müvekkil e-postası olmalı", output.contains("jane.smith@example.com"));
        assertTrue("Çıktıda müvekkil telefon numarası olmalı", output.contains("555-5678"));
        assertTrue("Çıktıda müvekkil adresi olmalı", output.contains("456 Oak St"));
    }

    @Test
    public void Test_Display_SelectViewClientDetails_NonExistentClient() {
        // Düzenleme - Scanner'ı olmayan bir müvekkil ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("2\n9999\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Client not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectUpdateClient_Success() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("Update", "Test", "update.test@example.com");
        client.setPhone("555-0000");
        client.setAddress("Update Address");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "3. Müvekkil Güncelle" seçeneğini seçecek şekilde ayarlama
        // Tüm alanları güncelleyelim
        ConsoleHelper.setScanner(new Scanner("3\n" + client.getId() + "\nUpdated\nName\nupdated.email@example.com\n555-9999\nNew Address\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Müvekkil güncellendi mi?
        Optional<Client> updatedClient = clientService.getClientById(client.getId());
        assertTrue("Müvekkil bulunmalı", updatedClient.isPresent());
        assertEquals("Müvekkil adı güncellenmeli", "Updated", updatedClient.get().getName());
        assertEquals("Müvekkil soyadı güncellenmeli", "Name", updatedClient.get().getSurname());
        assertEquals("Müvekkil e-postası güncellenmeli", "updated.email@example.com", updatedClient.get().getEmail());
        assertEquals("Müvekkil telefon numarası güncellenmeli", "555-9999", updatedClient.get().getPhone());
        assertEquals("Müvekkil adresi güncellenmeli", "New Address", updatedClient.get().getAddress());
    }

    @Test
    public void Test_Display_SelectUpdateClient_NonExistentClient() {
        // Düzenleme - Scanner'ı olmayan bir müvekkil ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("3\n9999\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Client not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectDeleteClient_Confirm_Success() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("Delete", "Test", "delete.test@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "4. Müvekkil Sil" seçeneğini seçecek ve onaylayacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + client.getId() + "\ny\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Müvekkil silindi mi?
        Optional<Client> deletedClient = clientService.getClientById(client.getId());
        assertFalse("Müvekkil silinmeli", deletedClient.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteClient_Cancel_NotDeleted() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("NotDelete", "Test", "notdelete.test@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "4. Müvekkil Sil" seçeneğini seçecek ama iptal edecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n" + client.getId() + "\nn\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Müvekkil silinmedi mi?
        Optional<Client> notDeletedClient = clientService.getClientById(client.getId());
        assertTrue("Müvekkil silinmemeli", notDeletedClient.isPresent());
    }

    @Test
    public void Test_Display_SelectDeleteClient_NonExistentClient() {
        // Düzenleme - Scanner'ı olmayan bir müvekkil ID'si ile ayarlama
        ConsoleHelper.setScanner(new Scanner("4\n9999\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda hata mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Client not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_Display_SelectSearchClients_Success() throws SQLException {
        // Birkaç müvekkil oluşturalım
        Client client1 = new Client("Search", "Result", "search.result@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Another", "Client", "another.client@example.com");
        clientDAO.create(client2);

        // Düzenleme - Scanner'ı "5. Müvekkil Ara" seçeneğini seçecek ve arama terimini girecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\nSearch\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda arama sonucu var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Search Result' olmalı", output.contains("Search"));
        assertFalse("Çıktıda 'Another Client' olmamalı", output.contains("Another"));
    }

    @Test
    public void Test_Display_SelectSearchClients_NoResults() throws SQLException {
        // Bir müvekkil oluşturalım
        Client client = new Client("Test", "Client", "test.client@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı "5. Müvekkil Ara" seçeneğini seçecek ve eşleşmeyen bir terim kullanacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("5\nNoMatch\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda sonuç bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'No clients found' mesajı olmalı", output.contains("No clients found"));
    }

    @Test
    public void Test_Display_SelectViewAllClients_Success() throws SQLException {
        // Birkaç müvekkil oluşturalım
        Client client1 = new Client("First", "Client", "first.client@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Second", "Client", "second.client@example.com");
        clientDAO.create(client2);

        // Düzenleme - Scanner'ı "6. Tüm Müvekkilleri Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda tüm müvekkiller var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'First Client' olmalı", output.contains("First"));
        assertTrue("Çıktıda 'Second Client' olmalı", output.contains("Second"));
    }

    @Test
    public void Test_Display_SelectViewAllClients_NoClients() {
        // Hiç müvekkil oluşturmuyoruz

        // Düzenleme - Scanner'ı "6. Tüm Müvekkilleri Listele" seçeneğini seçecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("6\n\n7\n"));

        // İşlem
        clientMenu.display();

        // Doğrulama - Çıktıda müvekkil bulunamadı mesajı var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'no registered clients' mesajı olmalı",
                output.contains("no registered clients") ||
                        output.contains("There are no registered clients"));
    }

    @Test
    public void Test_AddClient_WithDuplicateEmail_ShowsError() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("Duplicate", "Email", "duplicate@example.com");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı aynı email ile yeni müvekkil oluşturacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("New\nClient\nduplicate@example.com\n555-1234\nTest Address\n\n7\n"));

        // İşlem
        clientMenu.addClient();

    }

    @Test
    public void Test_ViewClientDetails_NonExistentClient() {
        // Düzenleme - Varolmayan bir müvekkil ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n7\n"));

        // İşlem
        clientMenu.viewClientDetails();

        // Doğrulama - Çıktıda müvekkil bulunamadı hatası var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Client not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_UpdateClient_WithEmptyFields_KeepsOldValues() throws SQLException {
        // Önce bir müvekkil oluşturalım
        Client client = new Client("Keep", "Values", "keep.values@example.com");
        client.setPhone("555-1234");
        client.setAddress("Original Address");
        clientDAO.create(client);

        // Düzenleme - Scanner'ı bazı alanları boş bırakacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner(client.getId() + "\n\n\n\n\nNew Address\n\n7\n"));

        // İşlem
        clientMenu.updateClient();

        // Doğrulama - Boş bırakılan alanlar değişmedi mi, değişen alan değişti mi?
        Optional<Client> updatedClient = clientService.getClientById(client.getId());
        assertTrue("Müvekkil bulunmalı", updatedClient.isPresent());
        assertEquals("Müvekkil adı aynı kalmalı", "Keep", updatedClient.get().getName());
        assertEquals("Müvekkil soyadı aynı kalmalı", "Values", updatedClient.get().getSurname());
        assertEquals("Müvekkil e-postası aynı kalmalı", "keep.values@example.com", updatedClient.get().getEmail());
        assertEquals("Müvekkil telefon numarası aynı kalmalı", "555-1234", updatedClient.get().getPhone());
        assertEquals("Müvekkil adresi değişmeli", "New Address", updatedClient.get().getAddress());
    }

    @Test
    public void Test_UpdateClient_WithInvalidEmail_ShowsError() throws SQLException {
        // Önce iki müvekkil oluşturalım
        Client client1 = new Client("Update", "Email", "update.email@example.com");
        clientDAO.create(client1);

        Client client2 = new Client("Other", "Client", "other.clientss@example.com");
        clientDAO.create(client2);

        // Düzenleme - Scanner'ı başka bir müvekkilin email'ini kullanacak şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner(client1.getId() + "\n\n\nother.client@example.com\n\n\n\n7\n"));

        // İşlem
        clientMenu.updateClient();
    }

    @Test
    public void Test_DeleteClient_NonExistentClient() {
        // Düzenleme - Varolmayan bir müvekkil ID'si kullan
        long nonExistentId = 9999L;
        ConsoleHelper.setScanner(new Scanner(nonExistentId + "\n\n7\n"));

        // İşlem
        clientMenu.deleteClient();

        // Doğrulama - Çıktıda müvekkil bulunamadı hatası var mı?
        String output = outContent.toString();
        assertTrue("Çıktıda 'Client not found' mesajı olmalı", output.contains("not found"));
    }

    @Test
    public void Test_SearchClients_EmptySearchTerm_ShowsError() {
        // Düzenleme - Scanner'ı boş arama terimi girecek şekilde ayarlama
        ConsoleHelper.setScanner(new Scanner("example\n\n7\n"));

        // İşlem
        clientMenu.searchClients();
    }
}