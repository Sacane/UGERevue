package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.service.QuestionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    void purge() {
        tagRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a tag")
    void createTag(){
        var tag = tagRepository.save(new Tag("Test"));
        assertNotNull(tag);
        assertEquals("Test", tag.getName());
    }

    @Test
    @DisplayName("Find tag by his name")
    void findTagByName(){
        var tag = tagRepository.save(new Tag("Test"));
        var findTag = tagRepository.findTagByName(tag.getName());
        assertNotNull(findTag);
        assertTrue(findTag.isPresent());
        assertEquals(findTag.get().getName(), tag.getName());
    }
}
