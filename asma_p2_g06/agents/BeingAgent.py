from mesa import Agent
from typing import Union

from agents.AgentType import AgentType

class PassiveBeingAgent(Agent):
    def __init__(self, unique_id, model, parameters):
        super().__init__(unique_id, model)
        self.parameters = parameters
        self.energy = parameters.initial_energy_amount

    def be_eaten(self):
        self.decrement_energy(self.energy)
        return self.parameters.energy_given_when_eaten

    def is_dead(self):
        return self.energy == 0
    
    def die(self): pass

    def decrement_energy(self, delta=1):
        self.energy = max(0, self.energy - delta)
        if self.energy == 0:
            self.model.add_agent_to_forget(self)

class ActiveBeingAgent(PassiveBeingAgent):
    def __init__(self, unique_id, model, parameters):
        super().__init__(unique_id, model, parameters)
        self.age = 0
        self.time_since_mate = 0

    def increment_attributes(self):
        self.age += 1
        if self.age >= self.parameters.max_age:
            self.be_eaten()
            return
        
        self.time_since_mate += 1

    def needs_to_eat(self):
        return self.energy < self.parameters.energy_threshold_to_eat

    def be_eaten(self):
        energy = self.energy
        self.decrement_energy(energy)
        return 10

    def eat_first_alive_agent(self, agents):
        for agent in agents:
            if agent.is_dead(): continue
            self.energy += agent.be_eaten()
            return

    def can_mate(self):
        return not self.is_dead() \
            and self.energy >= self.parameters.min_energy_to_mate \
            and self.time_since_mate >= self.parameters.mating_timeout

    def reset_time_since_mate(self):
        self.time_since_mate = 0
    
    def mate(self):
        self.reset_time_since_mate()
        self.model.generate_offsprint(self)

    def mate_with_first_free_agent(self, agents):
        if not self.can_mate(): return
        for agent in agents:
            if agent == self or not agent.can_mate(): continue
            agent.reset_time_since_mate()
            self.mate()
            return

    def is_dead(self):
        return self.energy == 0
    
    def die(self): pass

    def decrement_energy(self, delta=1):
        self.energy = max(0, self.energy - delta)
        if self.energy == 0:
            self.model.add_agent_to_forget(self)

    def get_agents_in_same_cell(self):
        agents = self.model.grid.get_cell_list_contents([ self.pos ])
        agents_categorized = {
            AgentType.Predator: [],
            AgentType.Prey: [],
            AgentType.Vegetation: []
        }

        for agent in agents:
            agents_categorized[agent.agent_type].append(agent)
        
        return agents_categorized

    def get_agents_in_neighbourhood(self, neighbourhood):
        agents_categorized = {
            AgentType.Predator: set(),
            AgentType.Prey: set(),
            AgentType.Vegetation: set()
        }

        for position in neighbourhood:
            agents = self.model.grid.get_cell_list_contents([ position ])
            for agent in agents:
                if agent == self: continue
                agents_categorized[agent.agent_type].add(position)

        for key in agents_categorized.keys():
            agents_categorized[key] = list(agents_categorized[key])
        
        return agents_categorized


    def move(self, to_mate: AgentType, to_eat: AgentType):
        possible_steps = self.model.grid.get_neighborhood(
            self.pos,
            moore=True, # moore includes all 8 surrounding squares, von neumann only top right left bot
            include_center=True # the agent can remain stationary
        )

        agents_in_neighbourhood = self.get_agents_in_neighbourhood(possible_steps)

        if len(agents_in_neighbourhood[to_eat]) > 0 and self.needs_to_eat():
            new_position = self.random.choice(agents_in_neighbourhood[to_eat])
        elif len(agents_in_neighbourhood[to_mate]) > 0 and self.can_mate():
            new_position = self.random.choice(agents_in_neighbourhood[to_mate])
        else:
            new_position = self.random.choice(possible_steps)

        self.model.grid.move_agent(self, new_position)
        self.decrement_energy()
