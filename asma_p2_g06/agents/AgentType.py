from enum import Enum, auto

class AgentType(Enum):
    def _generate_next_value_(name: str, start, count, last_values):
        return name.lower()

    Vegetation=auto(),
    Prey=auto(),
    Predator=auto()
