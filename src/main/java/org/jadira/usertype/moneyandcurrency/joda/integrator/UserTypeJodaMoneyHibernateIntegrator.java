/*
 *  Copyright 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.moneyandcurrency.joda.integrator;

import org.hibernate.integrator.spi.Integrator;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jadira.usertype.moneyandcurrency.joda.PersistentBigMoneyAmount;
import org.jadira.usertype.moneyandcurrency.joda.PersistentCurrencyUnit;
import org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount;
import org.jadira.usertype.spi.shared.AbstractUserTypeHibernateIntegrator;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;

public class UserTypeJodaMoneyHibernateIntegrator extends AbstractUserTypeHibernateIntegrator implements Integrator {

    private static UserType[] userTypes;
    private static final CompositeUserType[] COMPOSITE_USER_TYPES = new CompositeUserType[]{};

    static {

        boolean found = false;

        try {
            Class.forName("org.joda.money.BigMoney", false, ClassLoaderUtils.getClassLoader());
            found = true;
        } catch (ClassNotFoundException e) {
        }

        try {
            Class.forName("org.joda.money.BigMoney");
            found = true;
        } catch (ClassNotFoundException e) {
        }

        if (found) {

            userTypes = new UserType[]{
                    new PersistentBigMoneyAmount(),
                    new PersistentMoneyAmount(),
                    new PersistentCurrencyUnit()
            };

        } else {
            userTypes = new UserType[]{};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CompositeUserType[] getCompositeUserTypes() {
        return COMPOSITE_USER_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UserType[] getUserTypes() {
        return userTypes;
    }
}
