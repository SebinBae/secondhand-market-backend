package com.sebin.secondhand_market.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 모듈 경계 규칙 (CLAUDE.md 아키텍처 규칙과 1:1 대응).
 *
 * 허용 의존 방향: chat → product → user (단방향)
 * 크로스 도메인 접근: 상대 도메인의 service만 허용. repository 직접 접근 금지.
 * 엔티티 참조(@ManyToOne)는 현 단계에서 허용 — Phase 3 분리 확정 모듈에만 ID 참조 전환 (기획서 6번 참조).
 */
@AnalyzeClasses(
    packages = "com.sebin.secondhand_market",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ModuleBoundaryTest {

    // ── 의존 방향: 역방향 금지 ──────────────────────────────

    @ArchTest
    static final ArchRule user는_다른_도메인을_참조하지_않는다 =
        noClasses().that().resideInAPackage("..domain.user..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..domain.product..", "..domain.chat..");

    @ArchTest
    static final ArchRule product는_chat을_참조하지_않는다 =
        noClasses().that().resideInAPackage("..domain.product..")
            .should().dependOnClassesThat()
            .resideInAPackage("..domain.chat..");

    // ── 크로스 도메인 repository 직접 접근 금지 (서비스 경유 강제) ──

    @ArchTest
    static final ArchRule chat은_타_도메인_repository를_직접_쓰지_않는다 =
        noClasses().that().resideInAPackage("..domain.chat..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..domain.product.repository..",
                "..domain.user.repository.."
            );

    @ArchTest
    static final ArchRule product는_user의_repository를_직접_쓰지_않는다 =
        noClasses().that().resideInAPackage("..domain.product..")
            .should().dependOnClassesThat()
            .resideInAPackage("..domain.user.repository..");

    // ── 계층 규칙: controller는 repository를 건너뛰지 않는다 ──

    @ArchTest
    static final ArchRule controller는_repository를_직접_쓰지_않는다 =
        noClasses().that().resideInAPackage("..controller..")
            .should().dependOnClassesThat()
            .resideInAPackage("..repository..");

    // ── 예외 위치: global에 도메인 지식 금지 (예외 재배치 후 활성) ──

    @ArchTest
    static final ArchRule global_exception은_도메인을_참조하지_않는다 =
        noClasses().that().resideInAPackage("..global.exception..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..domain..");
}
