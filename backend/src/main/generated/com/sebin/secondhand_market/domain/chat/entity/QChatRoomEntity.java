package com.sebin.secondhand_market.domain.chat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoomEntity is a Querydsl query type for ChatRoomEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomEntity extends EntityPathBase<ChatRoomEntity> {

    private static final long serialVersionUID = 1532538233L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoomEntity chatRoomEntity = new QChatRoomEntity("chatRoomEntity");

    public final com.sebin.secondhand_market.domain.user.entity.QUserEntity buyer;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> lastMessageAt = createDateTime("lastMessageAt", java.time.LocalDateTime.class);

    public final com.sebin.secondhand_market.domain.product.entity.QProductEntity product;

    public final com.sebin.secondhand_market.domain.user.entity.QUserEntity seller;

    public QChatRoomEntity(String variable) {
        this(ChatRoomEntity.class, forVariable(variable), INITS);
    }

    public QChatRoomEntity(Path<? extends ChatRoomEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoomEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoomEntity(PathMetadata metadata, PathInits inits) {
        this(ChatRoomEntity.class, metadata, inits);
    }

    public QChatRoomEntity(Class<? extends ChatRoomEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new com.sebin.secondhand_market.domain.user.entity.QUserEntity(forProperty("buyer")) : null;
        this.product = inits.isInitialized("product") ? new com.sebin.secondhand_market.domain.product.entity.QProductEntity(forProperty("product"), inits.get("product")) : null;
        this.seller = inits.isInitialized("seller") ? new com.sebin.secondhand_market.domain.user.entity.QUserEntity(forProperty("seller")) : null;
    }

}

