package org.hildan.fxgson.factories;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A helper to handle {@link Type} and {@link TypeToken} objects creation.
 */
class TypeHelper {

    /**
     * Gets a {@link TypeToken} equivalent to the given one, but with the given raw type instead of the original one.
     *
     * @param sourceTypeToken
     *         the initial type token to get the type parameters from
     * @param newRawType
     *         the new raw type to use
     * @return a new type token with the given raw type and the type parameters of the given type token
     */
    @NotNull
    static TypeToken<?> withRawType(@NotNull TypeToken<?> sourceTypeToken, @NotNull Type newRawType) {
        ParameterizedType sourceType = (ParameterizedType) sourceTypeToken.getType();
        Type[] typeParams = sourceType.getActualTypeArguments();
        Type targetType = newParametrizedType(newRawType, typeParams);
        return TypeToken.get(targetType);
    }

    @NotNull
    private static ParameterizedType newParametrizedType(@NotNull Type rawType, @NotNull Type... typeArguments) {
        return new CustomParameterizedType(rawType, null, typeArguments);
    }

    private static class CustomParameterizedType implements ParameterizedType {

        private Type rawType;

        private Type ownerType;

        private Type[] typeArguments;

        private CustomParameterizedType(@NotNull Type rawType, @Nullable Type ownerType, @NotNull Type... typeArgs) {
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.typeArguments = typeArgs;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CustomParameterizedType that = (CustomParameterizedType) o;
            return Objects.equals(rawType, that.rawType) &&
                    Objects.equals(ownerType, that.ownerType) &&
                    Arrays.equals(typeArguments, that.typeArguments);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rawType, ownerType, typeArguments);
        }
    }
}
