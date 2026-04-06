package ru.kappers.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.kappers.model.leonmodels.LeagueLeon;
import ru.kappers.repository.LeagueLeonRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

//todo Переписать на тест с БД
@RunWith(MockitoJUnitRunner.class)
public class LeagueLeonServiceImplTest {
    @InjectMocks
    private LeagueLeonServiceImpl leagueLeonService;
    @Mock
    private LeagueLeonRepository repository;

    @Test
    public void getByName() {
        final String name = "test Name";
        final LeagueLeon leagueLeon = mock(LeagueLeon.class);
        when(repository.getByName(name)).thenReturn(leagueLeon);

        final LeagueLeon result = leagueLeonService.getByName(name);

        assertThat(result, is(leagueLeon));
        verify(repository).getByName(name);
    }

    @Test
    public void save() {
        final LeagueLeon leagueLeon = mock(LeagueLeon.class);
        when(repository.save(leagueLeon)).thenReturn(leagueLeon);

        final LeagueLeon result = leagueLeonService.save(leagueLeon);

        assertThat(result, is(leagueLeon));
        verify(repository).save(leagueLeon);
    }
}