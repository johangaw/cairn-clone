package com.example.cairnclone.game.actions

import com.example.cairnclone.game.Shaman

interface Action

// Player Actions
data class SpawnShaman(val shaman: Shaman): Action


// Internal Actions