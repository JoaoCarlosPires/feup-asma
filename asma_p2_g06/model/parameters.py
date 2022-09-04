from dataclasses import dataclass

@dataclass
class PassiveAgentParameters():
    initial_energy_amount: int
    energy_given_when_eaten: int

@dataclass
class AgentParameters(PassiveAgentParameters):
    max_age: int
    min_energy_to_mate: int
    mating_timeout: int
    energy_threshold_to_eat: int

@dataclass
class EcosystemParameters():
    predator_parameters: AgentParameters
    prey_parameters: AgentParameters
    vegetation_parameters: PassiveAgentParameters
    grid_width: int
    grid_height: int