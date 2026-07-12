package com.microfinance.user.mapper;

import com.microfinance.user.domain.model.User;
import com.microfinance.user.domain.model.UserDocument;
import com.microfinance.user.dto.DocumentResponse;
import com.microfinance.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "active", expression = "java(user.isActive())")
    @Mapping(target = "kycCompleted", expression = "java(user.isKycCompleted())")
    @Mapping(target = "premiumUser", expression = "java(user.isPremiumUser())")
    UserResponse toResponse(User user);

    @Mapping(target = "verified", expression = "java(document.isVerified())")
    @Mapping(target = "expired", expression = "java(document.isExpired())")
    @Mapping(target = "needsRenewal", expression = "java(document.needsRenewal())")
    DocumentResponse toDocumentResponse(UserDocument document);
}