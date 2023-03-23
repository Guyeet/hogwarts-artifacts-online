package edu.tcu.cs.hogwartsartifactsonline.wizard;
import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp(){
        this.wizards = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus DumbleDore");
        //w1.setArtifacts();
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        //w2.setArtifacts();
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");
        //w3.setArtifacts();
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown(){
    }

    @Test
    void testFindAllSuccess(){
        //Given
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        //When
        List<Wizard> realWizards = this.wizardService.findAll();

        //Then
        assertThat(realWizards.size()).isEqualTo(this.wizards.size());
        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess(){
        //Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        //When
        Wizard retWizard = this.wizardService.findById(1);

        //Then
        assertThat(retWizard.getId()).isEqualTo(wizard.getId());
        assertThat(retWizard.getName()).isEqualTo(wizard.getName());
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound(){
        //Given
        given(this.wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(() -> {
            Wizard retWizard = this.wizardService.findById(1);
        });

        //Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 1 :(");
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testSaveSuccess(){
        //Given
        Wizard nWizard = new Wizard();
        nWizard.setName("Some Wizard");

        given(this.wizardRepository.save(nWizard)).willReturn(nWizard);
        //When
        Wizard retWizard = this.wizardService.save(nWizard);

        //Then
        assertThat(retWizard.getName()).isEqualTo(nWizard.getName());
        verify(this.wizardRepository, times(1)).save(nWizard);
    }

    @Test
    void testUpdateSuccess(){
        //Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Albus Dumbledore");

        Wizard update = new Wizard();
        update.setName("New Wizard Name");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);
        //When
        Wizard updatedWizard = this.wizardService.update(1, update);

        //Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(this.wizardRepository,times(1)).findById(1);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound(){
        //Given
        Wizard update = new Wizard();
        update.setName("A new wizard name");

        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());
        //When
        assertThrows(ObjectNotFoundException.class, ()->{
            this.wizardService.update(1,update);
        });

        //Then
        verify(this.wizardRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteSuccess(){
        //Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).deleteById(1);
        //When
        this.wizardService.delete(1);

        //Then
        verify(this.wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound(){
        //Given
        given(this.wizardRepository.findById(1)).willReturn(Optional.empty());

        //When
        assertThrows(ObjectNotFoundException.class, ()->{
            this.wizardService.delete(1);
        });

        //Then
        verify(this.wizardRepository, times(1)).findById(1);
    }
}
