using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface IWheel : BaseInterface
    {
        float GetPressure();
        float GetMaxPressure();
        float GetMinPressure();
    }
}
