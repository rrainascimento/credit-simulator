package com.teste.creditas.credit_simulator.adapters.outbound.entity

import jakarta.persistence.*

@Entity
@Table(name = "loan_rates",
    uniqueConstraints = [UniqueConstraint(columnNames = ["min_age", "max_age"])])
data class InterestRateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "min_age", nullable = false)
    var minAge: Int,

    @Column(name = "max_age", nullable = false)
    var maxAge: Int,

    @Column(name = "annual_rate", nullable = false)
    var annualRate: Double
){
constructor() : this( 0, 0, 0.0)
}
