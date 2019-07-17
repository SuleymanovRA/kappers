package ru.kappers.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.kappers.model.Fixture;
import ru.kappers.model.utilmodel.FixtureFilter;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Data
public class FilteredFixtures {
    private List<Fixture> fixtures;
    private List<FixtureFilter> filters;

   public void addFilter (FixtureFilter filter){
       filters.add(filter);
   }

    public List<FixtureFilter> getFilters() {
        return filters;
    }

    public List<Fixture> refresh() throws IllegalAccessException, NoSuchFieldException, InstantiationException {
       List<Fixture> result = new ArrayList<>();
        for (Fixture f:fixtures) {
            for (FixtureFilter filter: filters) {
                if (f.getProperty(filter.getParamName()).equals(filter.getValue())){
                    result.add(f);
                }
            }
        }
        return result;
    }
}
