package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ParticleEffectSettings {
    @Builder.Default
    private float frameLength = (1f / 24);
    @Builder.Default
    private boolean loop = true;
}
