package com.yyon.grapplinghook.config.helper;

import java.lang.reflect.Field;
import java.util.Optional;

public interface IDefaultProvider {

    Optional<Object> getDefaultForField(Field field);

}
