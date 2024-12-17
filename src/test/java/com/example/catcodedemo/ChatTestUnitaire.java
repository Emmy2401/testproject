package com.example.catcodedemo;

import com.example.catcodedemo.Dto.ChatDTO;
import com.example.catcodedemo.Entity.Chat;
import com.example.catcodedemo.Repository.ChatRepository;
import com.example.catcodedemo.Service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//les test unitaires concernent le service
//car c'est la qu'on test les règles métiers
//le controller est un passe-plat : il reçoit une requête appel le service et retourne réponse donc on test plus l'intégration avec le controller
//pas de test sur repository méthode très simple et ici elles sont en plus fournies par JPA
public class ChatTestUnitaire {

    @InjectMocks
    private ChatService chatService; // classe qu'on va tester ici
    @Mock
    private ChatRepository chatRepository; //dépendance simulée (mockée)

    public ChatTestUnitaire() {
        MockitoAnnotations.openMocks(this); // Initialiser les simulations (mocks)

    }

    @Test
    public void testGetChatById_WhenChatExists() {
        // étape un : préparer des données
        Chat chat = new Chat();
        chat.setId(1L);
        chat.setNom("PomodoroCat");
        chat.setCouleur("Lila");
        chat.setGenre(false);
        chat.setDateNaissance(new Date(2024,12,15));

        // simulation du comportement de la méthode
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        //appel de la méthode
        ChatDTO resultat = chatService.getChatById(1L);

        //assertion : verifier les résultats
        assertNotNull(resultat);
        assertEquals(chat.getNom(), resultat.getNom());
        assertEquals(chat.getGenre(), resultat.getGenre());
        assertEquals(chat.getDateNaissance(), resultat.getDateNaissance());
        assertEquals(chat.getCouleur(), resultat.getCouleur());

        verify(chatRepository, times(1)).findById(1L); // Vérifier que le repository a été appelé une fois

    }
        @Test
        public void testUpdateChat_WhenChatExists() {
            // 1. Préparer les données pour le DTO à mettre à jour
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setId(1L);
            chatDTO.setNom("PomodoroCat");
            chatDTO.setCouleur("Lila");
            chatDTO.setGenre(false);
            chatDTO.setDateNaissance(new Date(2024, 12, 15));

            // 2. Préparer l'entité existante dans la base (simulée)
            Chat existingChat = new Chat();
            existingChat.setId(1L);
            existingChat.setNom("Pusheen");
            existingChat.setCouleur("Gris");
            existingChat.setGenre(true);
            existingChat.setDateNaissance(new Date(2022, 11, 10));

            // 3. Simuler le repository :
            // - findById(1L) renvoie le chat existant
            // - save(...) renvoie le chat passé en argument
            when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
            when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // 4. Appeler la méthode du service à tester
            ChatDTO updatedChatDTO = chatService.updateChat(chatDTO.getId(), chatDTO);

            // 5. Vérifier les résultats en utilisant les getters
            assertNotNull(updatedChatDTO, "Le résultat ne doit pas être null");
            assertEquals(chatDTO.getNom(), updatedChatDTO.getNom(), "Le nom doit être mis à jour");
            assertEquals(chatDTO.getCouleur(), updatedChatDTO.getCouleur(), "La couleur doit être mise à jour");
            assertEquals(chatDTO.getGenre(), updatedChatDTO.getGenre(), "Le genre doit être mis à jour");
            assertEquals(chatDTO.getDateNaissance(), updatedChatDTO.getDateNaissance(), "La date de naissance doit être mise à jour");
            assertEquals(chatDTO.getId(), updatedChatDTO.getId(), "L'ID doit rester celui du chat existant");

            // 6. Vérifier les interactions avec le repository
            verify(chatRepository, times(1)).findById(1L);
            verify(chatRepository, times(1)).save(any(Chat.class));
        }


    @Test
    public void testCreateChat_DataOk() {
        // 1. Préparer les données du test
        // On crée un ChatDTO avec toutes les informations nécessaires
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(null); // Lors d'une création, l'id est souvent null, c'est le save qui la génère
        chatDTO.setNom("PomodoroCat");
        chatDTO.setCouleur("Lila");
        chatDTO.setGenre(false);
        chatDTO.setDateNaissance(new Date(2024 , 11, 15));


        // 2. Simuler le comportement du repository
        // Quand on appelle chatRepository.save(...) avec n'importe quel Chat,
        // on simule le comportement suivant :
        // - On récupère l'argument (le Chat sauvegardé)
        // - On lui set un ID pour simuler que la base de données a généré un ID.
        // - On le retourne.
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat c = invocation.getArgument(0); // Récupère le chat passé en paramètre
            c.setId(1L);
            return c; // Retourne le chat modifié
        });

        // 3. Appeler la méthode du service à tester donc on la mock pas
        // On appelle createChat avec le chatDTO que l'on a préparé
        ChatDTO result = chatService.createChat(chatDTO);

        // 4. Vérifier les résultats
        // assertNotNull(result) -> On vérifie que le résultat n'est pas null
        assertNotNull(result);

        // On vérifie que le nom, la couleur, etc. sont restitués correctement.
        assertEquals(chatDTO.getNom(), result.getNom());
        assertEquals(chatDTO.getCouleur(), result.getCouleur());
        assertEquals(chatDTO.getGenre(), result.getGenre());
        assertEquals(chatDTO.getDateNaissance(), result.getDateNaissance());

        // On vérifie que l'id est désormais défini,
        // car on a simulé que la BD l'avait généré pendant le save
        assertEquals(1L, result.getId(), "L'ID devrait être défini après la sauvegarde");

        // 5. Vérifier les interactions avec le repository
        // On vérifie que chatRepository.save(...) a bien été appelé une fois
        verify(chatRepository, times(1)).save(any(Chat.class));

    }
    @Test
    public void testGetChatById_WhenChatDoesNotExist() {
        //préparation donnée
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        //exception levée
        RuntimeException exception = assertThrows(RuntimeException.class, () -> chatService.getChatById(1L));
        // vérifier que l'exception est bien appelé avec le bon message
        assertEquals("Le chat n'existe pas", exception.getMessage());
        // vérifier que le repository est appelé
        verify(chatRepository, times(1)).findById(1L);

    }
    @Test
    public void testUpdateChat_WhenChatDoesNotExist() {

        // Vérifier qu'une exception est levée lorsqu'on tente de mettre à jour un chat inexistant
        RuntimeException exception = assertThrows(RuntimeException.class, () -> chatService.updateChat(1L, new ChatDTO()));
        assertEquals("Le chat n'existe pas", exception.getMessage());

        // Vérifier que save n'a jamais été appelé, car pas de chat à mettre à jour
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    public void testDeleteChat_WhenChatDoesNotExist() {
        // Simuler qu'aucun chat n'existe pour l'ID 1
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        // Vérifier qu'une exception est levée lorsqu'on tente de supprimer
        RuntimeException exception = assertThrows(RuntimeException.class, () -> chatService.deleteChatById(1L));
        assertEquals("Le chat n'existe pas", exception.getMessage());

        // Vérifier que deleteById n'a jamais été appelé, car pas de chat à supprimer
        verify(chatRepository, never()).deleteById(1L);
    }

    @Test
    public void testDeleteChat_WhenChatExists() {
        // étape un : préparer des données
        Chat chat = new Chat();
        chat.setId(1L);
        chat.setNom("PomodoroCat");
        chat.setCouleur("Lila");
        chat.setGenre(false);
        chat.setDateNaissance(new Date(2024,12,15));
        //simuler le comportement du repository
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        chatService.deleteChatById(chat.getId());
        // Vérifier que deleteById a été appelé
        verify(chatRepository, times(1)).deleteById(chat.getId());
    }
}
