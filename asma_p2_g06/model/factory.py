from model import AgentParameters, PassiveAgentParameters, EcosystemParameters

def get_model_parameters():
    return EcosystemParameters(
        predator_parameters=AgentParameters(
            initial_energy_amount=10,
            energy_given_when_eaten=0, # doesn't matter, predators won't be eaten

            max_age=60,

            mating_timeout=10,
            min_energy_to_mate=8,

            energy_threshold_to_eat=10
        ),

        prey_parameters=AgentParameters(
            initial_energy_amount=10,
            energy_given_when_eaten=10,

            max_age=75,
            
            mating_timeout=5,
            min_energy_to_mate=5,

            energy_threshold_to_eat=20
        ),

        vegetation_parameters=PassiveAgentParameters(
            initial_energy_amount=10,
            energy_given_when_eaten=10
        ),

        grid_width=20,
        grid_height=20
    )
